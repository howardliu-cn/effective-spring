package cn.howard.demo.springfilter.controller;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;

/**
 * TODO 描述信息
 *
 * @author liuxinghao
 * @version 1.0
 * @since 2021/4/29 下午3:52
 */
@Controller
@RequestMapping("/index")
public class IndexController {
    @GetMapping("/get")
    @ResponseBody
    public String get(String name) {
        return "Hello, " + name;
    }

    @PostMapping("/post")
    @ResponseBody
    public Map<String, String> postBody(@RequestBody Map<String, String> params) {
        final Map<String, String> result = Maps.newHashMap(params);
        result.put("timestamp", System.currentTimeMillis() + "");
        return result;
    }

    @PostMapping("/upload")
    @ResponseBody
    public Map<String, String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        final Map<String, String> result = Maps.newHashMap();
        result.put("contentType", file.getContentType());
        result.put("contentLength", file.getBytes().length + "");
        return result;
    }

    @GetMapping("/download")
    public String download(HttpServletResponse response) {
        final File file = new File("/Users/liuxh/history.txt");
        if (!file.exists()) {
            return "下载文件不存在";
        }
        response.reset();
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("utf-8");
        response.setContentLength((int) file.length());
        response.setHeader("Content-Disposition", "attachment;filename=" + System.currentTimeMillis() + ".txt");

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));) {
            byte[] buff = new byte[1024];
            OutputStream os = response.getOutputStream();
            int i = 0;
            while ((i = bis.read(buff)) != -1) {
                os.write(buff, 0, i);
                os.flush();
            }
        } catch (IOException e) {
            return "下载失败";
        }
        return "下载成功";
    }
}
