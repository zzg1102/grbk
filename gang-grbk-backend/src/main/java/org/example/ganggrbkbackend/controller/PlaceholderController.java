package org.example.ganggrbkbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * 占位符图片控制器
 *
 * @author Gang
 */
@Tag(name = "占位符图片", description = "生成占位符图片")
@RestController
@RequestMapping("/placeholder")
public class PlaceholderController {

    @Operation(summary = "生成占位符图片")
    @GetMapping("/{width}/{height}")
    public ResponseEntity<byte[]> generatePlaceholder(
            @Parameter(description = "图片宽度", required = true)
            @PathVariable Integer width,
            @Parameter(description = "图片高度", required = true)
            @PathVariable Integer height) throws IOException {

        // 限制图片大小
        width = Math.min(Math.max(width, 16), 800);
        height = Math.min(Math.max(height, 16), 600);

        // 创建图片
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // 设置背景色
        g2d.setColor(new Color(240, 240, 240));
        g2d.fillRect(0, 0, width, height);

        // 设置边框
        g2d.setColor(new Color(200, 200, 200));
        g2d.drawRect(0, 0, width - 1, height - 1);

        // 添加文字
        g2d.setColor(new Color(150, 150, 150));
        String text = width + " × " + height;
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();

        int x = (width - textWidth) / 2;
        int y = (height + textHeight) / 2;

        if (width > 60 && height > 20) {
            g2d.drawString(text, x, y);
        }

        g2d.dispose();

        // 转换为字节数组
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] imageBytes = baos.toByteArray();

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(imageBytes);
    }
}