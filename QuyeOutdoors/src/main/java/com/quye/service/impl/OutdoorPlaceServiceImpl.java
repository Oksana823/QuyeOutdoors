package com.quye.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quye.dto.Result;
import com.quye.entity.OutdoorPlace;
import com.quye.mapper.OutdoorPlaceMapper;
import com.quye.service.IOutdoorPlaceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.quye.utils.CacheClient;
import com.quye.utils.RedisConstants;
import com.quye.utils.RedisData;
import com.quye.utils.SystemConstants;
import jakarta.annotation.Resource;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.quye.utils.RedisConstants.*;

@Service
public class OutdoorPlaceServiceImpl extends ServiceImpl<OutdoorPlaceMapper, OutdoorPlace> implements IOutdoorPlaceService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private CacheClient  cacheClient;

    @Override
    public Result queryById(Long id) {
        //缓存穿透
        OutdoorPlace place = cacheClient.queryWithPassThrough(CACHE_PLACE_KEY, id,
                CACHE_PLACE_TTL, TimeUnit.MINUTES, OutdoorPlace.class, this::getById);
        if(place == null){
            return  Result.fail("The OutdoorPlace doesn't exist!!!!");
        }
        return Result.ok(place);
    }
//
//
//
    public OutdoorPlace queryWithPassThrough(Long id){
        String placeJson = stringRedisTemplate.opsForValue().get(CACHE_PLACE_KEY+ id);
        if(StrUtil.isNotBlank(placeJson)){
            OutdoorPlace place = JSONUtil.toBean(placeJson,OutdoorPlace.class);
            return place;
        }
        if(placeJson != null){
            return  null;
        }
        OutdoorPlace place = getById(id);
        if(place == null){
            //将空值写入redis
            stringRedisTemplate.opsForValue().set(CACHE_PLACE_KEY+ id,"",CACHE_NULL_TTL, TimeUnit.MINUTES);
            return  null;
        }
        stringRedisTemplate.opsForValue().set(CACHE_PLACE_KEY+ id,JSONUtil.toJsonStr(place),CACHE_PLACE_TTL, TimeUnit.MINUTES);
        return place;
    }
    @Override
    @Transactional
    public Result update(OutdoorPlace place) {
        Long placeId = place.getId();
        if(placeId == null){
            return Result.fail("目的地ID不能为空");
        }
        updateById(place);
        stringRedisTemplate.delete(CACHE_PLACE_KEY + placeId);

        return Result.ok();
    }

    @Override
    public Result queryOutdoorPlaceByType(Integer categoryId, Integer current, Double x, Double y) {
        if (x == null || y == null) {
            // 不需要坐标查询，按数据库查询
            Page<OutdoorPlace> page = query()
                    .eq("category_id", categoryId)
                    .page(new Page<>(current, SystemConstants.DEFAULT_PAGE_SIZE));
            return Result.ok(page.getRecords());
        }

        int from = (current - 1) * SystemConstants.DEFAULT_PAGE_SIZE;
        int end = current * SystemConstants.DEFAULT_PAGE_SIZE;

        String key = PLACE_GEO_KEY + categoryId;
        GeoResults<RedisGeoCommands.GeoLocation<String>> results = stringRedisTemplate.opsForGeo() // GEOSEARCH key BYLONLAT x y BYRADIUS 10 WITHDISTANCE
                .search(
                        key,
                        GeoReference.fromCoordinate(x, y),
                        new Distance(5000),
                        RedisGeoCommands.GeoSearchCommandArgs.newGeoSearchArgs().includeDistance().limit(end)
                );
        if (results == null) {
            return Result.ok(Collections.emptyList());
        }
        List<GeoResult<RedisGeoCommands.GeoLocation<String>>> list = results.getContent();
        if (list.size() <= from) {
            // 没有下一页了，结束
            return Result.ok(Collections.emptyList());
        }
        List<Long> ids = new ArrayList<>(list.size());
        Map<String, Distance> distanceMap = new HashMap<>(list.size());
        list.stream().skip(from).forEach(result -> {
            String placeIdStr = result.getContent().getName();
            ids.add(Long.valueOf(placeIdStr));
            Distance distance = result.getDistance();
            distanceMap.put(placeIdStr, distance);
        });
        String idStr = StrUtil.join(",", ids);
        List<OutdoorPlace> places = query().in("id", ids).last("ORDER BY FIELD(id," + idStr + ")").list();
        for (OutdoorPlace place : places) {
            place.setDistance(distanceMap.get(place.getId().toString()).getValue());
        }
        return Result.ok(places);
    }
}
