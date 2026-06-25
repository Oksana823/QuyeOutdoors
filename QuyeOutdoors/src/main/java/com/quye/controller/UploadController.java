package com.quye.controller;

import cn.hutool.core.util.StrUtil;
import com.quye.dto.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/uploads")
public class UploadController {

    private static final Set<String> ALLOWED_SUFFIXES = Set.of("jpg", "jpeg", "png", "webp", "gif");
    private final Path uploadRoot;

    public UploadController(@Value("${quye.upload-dir}") String uploadDir) {
        this.uploadRoot = Path.of(uploadDir).toAbsolutePath().normalize();
    }

    @PostMapping("/notes")
    public Result uploadImage(@RequestParam("file") MultipartFile image) {
        if (image == null || image.isEmpty()) {
            return Result.fail("请选择要上传的图片");
        }
        String contentType = image.getContentType();
        if (contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            return Result.fail("仅支持图片文件");
        }
        try {
            String fileName = createNewFileName(image.getOriginalFilename());
            if (fileName == null) {
                return Result.fail("仅支持 jpg、png、webp 或 gif 图片");
            }
            Path target = resolveJourneyFile(fileName);
            Files.createDirectories(target.getParent());
            image.transferTo(target);
            log.debug("文件上传成功，{}", fileName);
            return Result.ok("/imgs" + fileName);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.fail("文件上传失败");
        }
    }

    @GetMapping("/notes/delete")
    public Result deleteJourneyNoteImg(@RequestParam("name") String filename) {
        if (StrUtil.isBlank(filename)) {
            return Result.fail("错误的文件名称");
        }
        String normalizedName = normalizeJourneyName(filename);
        if (!normalizedName.startsWith("/journeys/")) {
            return Result.fail("错误的文件名称");
        }
        try {
            Path file = resolveJourneyFile(normalizedName);
            if (Files.isDirectory(file)) {
                return Result.fail("错误的文件名称");
            }
            Files.deleteIfExists(file);
            return Result.ok();
        } catch (IOException | IllegalArgumentException e) {
            log.warn("删除上传图片失败：{}", filename, e);
            return Result.fail("图片删除失败");
        }
    }

    private String createNewFileName(String originalFilename) {
        if (StrUtil.isBlank(originalFilename) || !originalFilename.contains(".")) {
            return null;
        }
        String suffix = StrUtil.subAfter(originalFilename, ".", true).toLowerCase(Locale.ROOT);
        if (!ALLOWED_SUFFIXES.contains(suffix)) {
            return null;
        }
        String name = UUID.randomUUID().toString();
        int hash = name.hashCode();
        int d1 = hash & 0xF;
        int d2 = (hash >> 4) & 0xF;
        return StrUtil.format("/journeys/{}/{}/{}.{}", d1, d2, name, suffix);
    }

    private Path resolveJourneyFile(String filename) {
        String relative = filename.replace('\\', '/');
        while (relative.startsWith("/")) {
            relative = relative.substring(1);
        }
        Path target = uploadRoot.resolve(relative).normalize();
        if (!target.startsWith(uploadRoot)) {
            throw new IllegalArgumentException("图片路径越界");
        }
        return target;
    }
    private String normalizeJourneyName(String filename) {
        String normalized = filename.replace('\\', '/');
        if (normalized.startsWith("/imgs/journeys/")) {
            return normalized.substring("/imgs".length());
        }
        return normalized;
    }
}