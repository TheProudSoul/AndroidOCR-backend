package SADemo.Model;

import net.sourceforge.tess4j.ITesseract;

import java.io.File;

public class TesseractExpert {
    private static String tessdata = "D:\\academic\\SA\\project\\AndroidOCR-backend\\src\\SADemo\\tessdata";
//    private static String tessdata = "/usr/local/WebData/tessdata";
//    static public ITesseract getInstance() {
//        if (instance == null) {
//            instance = new net.sourceforge.tess4j.Tesseract();
//            instance.setDatapath(tessdata);
//        }
//        return instance;
//    }
//static ITesseract instance;
    private TesseractExpert() {
        if (SingletonHolder.instance != null) {
            throw new IllegalStateException();
        }
    }

    private static class SingletonHolder {

        private static ITesseract instance = new net.sourceforge.tess4j.Tesseract();
    }

    public static ITesseract getInstance() {
        return SingletonHolder.instance;
    }
    // Tesseract单例
//    private static ITesseract instance = null;
//    private TesseractExpert() {}
//    public static ITesseract getInstance() {
//        if (instance == null) {
//            synchronized (TesseractExpert.class) {
//                if (instance == null)
//                    instance = new net.sourceforge.tess4j.Tesseract();
//                    instance.setDatapath(tessdata);
//                }
//        }
//        return instance;
//    }


    //使用英文字库 - 识别图片
    public static String testEn(File imageFile) throws Exception {
        ITesseract instance = getInstance(); //识别
        instance.setDatapath(tessdata);
        String result = instance.doOCR(imageFile);
        System.out.println(result);
        return result;
    }
}
