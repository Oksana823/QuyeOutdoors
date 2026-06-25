package com.quye.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quye.dto.Result;
import com.quye.dto.UserDTO;
import com.quye.entity.JourneyNote;
import com.quye.entity.User;
import com.quye.service.IJourneyNoteService;
import com.quye.service.IUserService;
import com.quye.utils.SystemConstants;
import com.quye.utils.UserHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/notes")
public class JourneyNoteController {

    @Resource
    private IJourneyNoteService journeyNoteService;


    @PostMapping
    public Result publishNote(@RequestBody JourneyNote note) {

        return journeyNoteService.publishNote(note);
    }

    @PutMapping("/like/{id}")
    public Result likeJourneyNote(@PathVariable("id") Long id) {
        return journeyNoteService.likeJourneyNote(id);
    }

    @GetMapping("/of/me")
    public Result queryMyJourneyNote(@RequestParam(value = "current", defaultValue = "1") Integer current) {
        UserDTO user = UserHolder.getUser();
        Page<JourneyNote> page = journeyNoteService.query()
                .eq("user_id", user.getId()).page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        List<JourneyNote> records = page.getRecords();
        return Result.ok(records);
    }

    @GetMapping("/hot")
    public Result queryHotJourneyNote(@RequestParam(value = "current", defaultValue = "1") Integer current) {
        return journeyNoteService.queryHotNotes(current);
    }
    @GetMapping("/{id}")
    public Result queryJourneyNoteById(@PathVariable("id") Long id) {
        return journeyNoteService.queryJourneyNoteById(id);
    }

    @GetMapping("/likes/{id}")
    public Result queryJourneyNoteLikes(@PathVariable("id") Long id) {
        return journeyNoteService.queryJourneyNoteLikes(id);
    }
    @GetMapping("/of/users")
    public Result queryJourneyNoteByUserId(
            @RequestParam(value = "current", defaultValue = "1") Integer current,
            @RequestParam("id") Long id) {
        Page<JourneyNote> page = journeyNoteService.query()
                .eq("user_id", id).page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        List<JourneyNote> records = page.getRecords();
        return Result.ok(records);
    }
    @GetMapping("/of/follows")
    public Result queryJourneyNoteOfFollow(
            @RequestParam("lastId") Long max, @RequestParam(value = "offset", defaultValue = "0") Integer offset){
        return journeyNoteService.queryJourneyNoteOfFollow(max, offset);
    }

}
