package SADemo;

import SADemo.Model.ImageExpert;
import SADemo.Model.TesseractExpert;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class ImageServlet extends HttpServlet {
    // 利用separator做分隔符拼接目录
//    private static final String separator = File.separator;
    // 上传文件存储目录
    private static final String UPLOAD_DIRECTORY = "upload";
    private static final String GREY_DIRECTORY = "grey";
    // tessdata目录
    private static final String TESS_DIRECTORY = "tessdata";

    // 上传配置
    private static final int MEMORY_THRESHOLD   = 1024 * 1024 * 3;  // 3MB
    private static final int MAX_FILE_SIZE      = 1024 * 1024 * 10; // 40MB
    private static final int MAX_REQUEST_SIZE   = 1024 * 1024 * 20; // 50MB

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long totalStartTime = System.nanoTime();    //获取开始时间
        System.out.println("ImageServlet：");
        response.setContentType("application/json; charset=utf-8");
        request.setCharacterEncoding("utf-8");  //设置编码
        if(!ServletFileUpload.isMultipartContent(request)){
            PrintWriter writer = response.getWriter();
            writer.println("Error: No image file received");
        }

        // 配置上传参数
        DiskFileItemFactory factory = new DiskFileItemFactory();
        // 设置内存临界值 - 超过后将产生临时文件并存储于临时目录中
        factory.setSizeThreshold(MEMORY_THRESHOLD);
        // 设置临时存储目录
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

        ServletFileUpload imageUpload = new ServletFileUpload(factory);
        imageUpload.setFileSizeMax(MAX_FILE_SIZE);// 设置最大文件上传值
        imageUpload.setSizeMax(MAX_REQUEST_SIZE);// 设置最大请求值 (包含文件和表单数据)
        imageUpload.setHeaderEncoding("UTF-8");// 中文处理

        // 构造临时路径来存储上传的文件
        // 这个路径相对当前应用的目录
        String uploadPath = request.getServletContext().getRealPath("./") + UPLOAD_DIRECTORY;
        String greyPath = request.getServletContext().getRealPath("./") + GREY_DIRECTORY;
//        String tessdataPath = request.getServletContext().getRealPath("./") + TESS_DIRECTORY;

        System.out.println(uploadPath);
        System.out.println(greyPath);
        // 如果目录不存在则创建
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }
        File greyDir = new File(greyPath);
        if (!greyDir.exists()) {
            greyDir.mkdir();
        }

        try {
            // 解析请求的内容提取文件数据
            @SuppressWarnings("unchecked")
            List<FileItem> imageItems = imageUpload.parseRequest(request);

            if (imageItems != null && imageItems.size() > 0) {
                for (FileItem item: imageItems){
                    //获取路径名
                    String value = item.getName() ;
                    System.out.println("文件名："+value);
                    //索引到最后一个反斜杠
                    int start = value.lastIndexOf("\\");
                    //截取上传文件的字符串名字，加1是 去掉反斜杠，
                    String filename = value.substring(start+1);
                    request.setAttribute("image_file", filename);
                    //写到磁盘上
                    item.write( new File(uploadPath,filename) );//第三方提供的
                    ImageExpert imageExpert = new ImageExpert();
                    System.out.println(uploadPath+'\\'+filename);
                    File file = new File(uploadPath+'\\'+filename);
                    long startTime=System.nanoTime();
                    File greyImg = new File(imageExpert.cleanImage(file, greyPath));
                    long endTime = System.nanoTime();
                    System.out.println("后台处理图片耗时========= : "+ (endTime-startTime)+"ns");
//                    String greyImg = imageExpert.cleanImage(file, greyPath);
                    startTime=System.nanoTime();
                    String result = TesseractExpert.testEn(greyImg);
                    endTime = System.nanoTime();
                    System.out.println("后台调用接口耗时========= : "+ (endTime-startTime)+"ns");
                    System.out.println("上传成功：" + filename);
                    response.getWriter().print(result);//将路径返回给客户端
                }
            }
            long totalEndTime = System.nanoTime();    //获取结束时间
            System.out.println("ImageServlet后台全部运行时间：" + (totalEndTime - totalStartTime) + "ns");
        }catch (Exception ex) {
            System.out.println("上传失败");
            request.setAttribute("message", "上传失败：" + ex.getMessage());
        }
    }
}
