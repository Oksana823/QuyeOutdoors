package com.quye.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quye.dto.Result;
import com.quye.dto.ScrollResult;
import com.quye.dto.UserDTO;
import com.quye.entity.JourneyNote;
import com.quye.entity.Follow;
import com.quye.entity.User;
import com.quye.mapper.JourneyNoteMapper;
import com.quye.service.IJourneyNoteService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.quye.service.IFollowService;
import com.quye.service.IUserService;
import com.quye.utils.SystemConstants;
import com.quye.utils.UserHolder;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.quye.utils.RedisConstants.NOTE_LIKED_KEY;
import static com.quye.utils.RedisConstants.FOLLOW_FEED_KEY;

@Service
public class JourneyNoteServiceImpl extends ServiceImpl<JourneyNoteMapper, JourneyNote> implements IJourneyNoteService {

    @Resource
    private IUserService userService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private IFollowService followService;
    @Override
    public Result queryHotNotes(Integer current) {
        Page<JourneyNote> page = query()
                .orderByDesc("liked")
                .page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        List<JourneyNote> records = page.getRecords();
        records.forEach(note -> {
            this.fillNoteAuthor(note);
            this.isNoteLiked(note);
        });

        return Result.ok(records);
    }

    @Override
    public Result queryJourneyNoteById(Long id) {
        JourneyNote note = getById(id);
        if(note == null){
            return Result.fail("The note doesn't exist");
        }
        fillNoteAuthor(note);
        isNoteLiked(note);
        return Result.ok(note);
    }

    private void isNoteLiked(JourneyNote note) {
        UserDTO user = UserHolder.getUser();
        if(user == null){
            //not logged yet
            return;
        }
        Long userId = user.getId();
        String key = "quye:note:liked:" + note.getId();
        Double score = stringRedisTemplate.opsForZSet().score(key, userId.toString());
        note.setIsLike(score!=null);
    }

    @Override
    public Result likeJourneyNote(Long id) {
        Long userId = UserHolder.getUser().getId();
        String key = "quye:note:liked:" + id;
        Double score = stringRedisTemplate.opsForZSet().score(key, userId.toString());
        if(score == null){
            boolean isSuccess = update().setSql("liked = liked + 1").eq("id", id).update();
            if(isSuccess){
                stringRedisTemplate.opsForZSet().add(key,userId.toString(),System.currentTimeMillis());
            }
        }
        else {
            boolean isSuccess = update().setSql("liked = liked - 1").eq("id", id).update();
            if(isSuccess){
                stringRedisTemplate.opsForZSet().remove(key,userId.toString());
            }
        }
        return Result.ok();
    }

    @Override
    public Result queryJourneyNoteLikes(Long id) {
        String key = NOTE_LIKED_KEY + id;
        Set<String> top5 = stringRedisTemplate.opsForZSet().range(key, 0, 4);
        if (top5 == null || top5.isEmpty()) {
            return Result.ok(Collections.emptyList());
        }
        List<Long> ids = top5.stream().map(Long::valueOf).collect(Collectors.toList());
        String idStr = StrUtil.join(",", ids);
        List<UserDTO> userDTOS = userService.query()
                .in("id", ids).last("ORDER BY FIELD(id," + idStr + ")").list()
                .stream()
                .map(user -> BeanUtil.copyProperties(user, UserDTO.class))
                .collect(Collectors.toList());
        return Result.ok(userDTOS);
    }

    @Override
    public Result publishNote(JourneyNote note) {
        if (note == null || note.getPlaceId() == null) {
            return Result.fail("请选择目的地");
        }
        if (StrUtil.isBlank(note.getTitle())) {
            return Result.fail("标题不能为空");
        }
        if (StrUtil.isBlank(note.getContent())) {
            return Result.fail("正文不能为空");
        }
        if (StrUtil.isBlank(note.getImages())) {
            return Result.fail("请至少上传一张图片");
        }
        List<String> images = StrUtil.splitTrim(note.getImages(), ',');
        if (images.size() > 9) {
            return Result.fail("最多上传9张图片");
        }
        note.setTitle(note.getTitle().trim());
        note.setContent(note.getContent().trim());
        note.setImages(String.join(",", images));
        UserDTO user = UserHolder.getUser();
        note.setUserId(user.getId());
        boolean isSuccess = save(note);
        if(!isSuccess){
            return Result.fail("新增失败");
        }
        List<Follow> follows = followService.query().eq("follow_user_id", user.getId()).list();
        for (Follow follow : follows) {
            Long userId = follow.getUserId();
            String key = FOLLOW_FEED_KEY + userId;
            stringRedisTemplate.opsForZSet().add(key, note.getId().toString(), System.currentTimeMillis());
        }
        return Result.ok(note.getId());
    }

    @Override
    public Result queryJourneyNoteOfFollow(Long max, Integer offset) {
        Long userId = UserHolder.getUser().getId();
        String key = FOLLOW_FEED_KEY + userId;
        Set<ZSetOperations.TypedTuple<String>> typedTuples = stringRedisTemplate.opsForZSet()
                .reverseRangeByScoreWithScores(key, 0, max, offset, 2);
        if (typedTuples == null || typedTuples.isEmpty()) {
            return Result.ok();
        }
        List<Long> ids = new ArrayList<>(typedTuples.size());
        long minTime = 0; // 2
        int os = 1; // 2
        for (ZSetOperations.TypedTuple<String> tuple : typedTuples) { // 5 4 4 2 2
            ids.add(Long.valueOf(tuple.getValue()));
            long time = tuple.getScore().longValue();
            if(time == minTime){
                os++;
            }else{
                minTime = time;
                os = 1;
            }
        }
        os = minTime == max ? os : os + offset;
        String idStr = StrUtil.join(",", ids);
        List<JourneyNote> notes = query().in("id", ids).last("ORDER BY FIELD(id," + idStr + ")").list();

        for (JourneyNote note : notes) {
            fillNoteAuthor(note);
            isNoteLiked(note);
        }

        ScrollResult r = new ScrollResult();
        r.setList(notes);
        r.setOffset(os);
        r.setMinTime(minTime);

        return Result.ok(r);
    }

    private void fillNoteAuthor(JourneyNote note) {
        Long userId = note.getUserId();
        User user = userService.getById(userId);
        note.setName(user.getNickName());
        note.setIcon(user.getIcon());
    }
}
