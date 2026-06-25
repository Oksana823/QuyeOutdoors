package com.quye.service.impl;

import com.quye.entity.NoteComment;
import com.quye.mapper.NoteCommentMapper;
import com.quye.service.INoteCommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class NoteCommentServiceImpl extends ServiceImpl<NoteCommentMapper, NoteComment> implements INoteCommentService {

}
