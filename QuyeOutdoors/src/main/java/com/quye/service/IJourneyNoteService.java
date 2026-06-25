package com.quye.service;

import com.quye.dto.Result;
import com.quye.entity.JourneyNote;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IJourneyNoteService extends IService<JourneyNote> {

    Result queryHotNotes(Integer current);

    Result queryJourneyNoteById(Long id);

    Result likeJourneyNote(Long id);

    Result queryJourneyNoteLikes(Long id);

    Result publishNote(JourneyNote note);

    Result queryJourneyNoteOfFollow(Long max, Integer offset);
}
