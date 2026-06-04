package company.restaurant.controller;
import company.restaurant.exception.BusinessException;
import company.restaurant.util.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

//处理图片URL
@RestController
@Slf4j
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class FileUploadController {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.upload.url-prefix}")
    private String urlPrefix;

    @PostMapping("/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        // 1. 校验文件后缀安全性
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new BusinessException("非法文件名");
        }

        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        if (!List.of(".jpg", ".jpeg", ".png", ".webp").contains(suffix)) {
            throw new BusinessException("只支持 jpg/png/webp 格式");
        }

        // 2. 构造安全的文件存储路径
        String newFilename = UUID.randomUUID() + suffix;
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        // 使用 Paths 自动处理跨平台的路径分隔符
        Path targetDir = Paths.get(uploadPath, datePath);
        Path targetFile = targetDir.resolve(newFilename);

        try {
            // 创建文件夹
            Files.createDirectories(targetDir);

            // 🔥 优化点 1：使用 toAbsolutePath() 彻底避免 Tomcat 临时目录残留 Bug
            file.transferTo(targetFile.toAbsolutePath().toFile());

            // 也可以选择更现代的 NIO 方式（二选一）：
            // Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            log.error("文件上传保存失败，路径：{}", targetFile, e);
            throw new BusinessException("服务器内部存储失败");
        }

        // 3. 🔥 优化点 2：处理 URL 拼接斜杠问题
        // 去除 urlPrefix 末尾的斜杠，防止拼接出双斜杠 "//"
        String safeUrlPrefix = urlPrefix.endsWith("/") ? urlPrefix.substring(0, urlPrefix.length() - 1) : urlPrefix;
        String imageUrl = safeUrlPrefix + "/" + datePath + "/" + newFilename;

        log.info("文件上传成功，imageUrl:{}", imageUrl);
        return Result.success(imageUrl);
    }
}