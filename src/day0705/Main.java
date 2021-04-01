package day0705;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import net.m3u8.main.Down;

import java.io.*;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Main {

    public final static String MyHostURL = "/Users/mobai/Downloads/Java/";

    /**
     * 验证信息——Cookie里获取
     */
    static class User {
        private List<Integer> courses;
        private String studentId;
        private String _uc_t_;

        public User(String config) throws Exception {
            JSONObject object = JSON.parseObject(readString(config));
            this._uc_t_ = object.getString("_uc_t_");
            this.studentId = object.getString("studentId");
            this.courses = object.getObject("courseIds", List.class);
        }

        public List<Integer> getCourses() {
            return courses;
        }

        public String getStudentId() {
            return studentId;
        }

        public String get_uc_t_() {
            return _uc_t_;
        }
    }

    static String readString(String name) {
        try {
            File file = new File(name);
            byte[] bytes = new byte[(int) file.length()];
            FileInputStream fis = new FileInputStream(file);
            fis.read(bytes);
            fis.close();
            return new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    static void writeString(String name, String data) {
        try {
            FileOutputStream fos = new FileOutputStream(name);
            fos.write(data.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 请使用JDK11来执行，否则可能会有解密错误
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        //1.创建目录
        try {
            new File(MyHostURL + "data").mkdir();
            new File(MyHostURL + "video").mkdir();

//            System.out.println(File.separator);
//            System.out.println(File.pathSeparator);

        } catch (Exception e) {
            e.printStackTrace();
        }

        //2.检测参数
        switch (args.length) {
            case 0:
                getVideoInfo(new User("user.json"));
                break;
            case 1:
                getVideoInfo(new User(args[0]));
                break;
            case 2:
                if ("d".equals(args[0])) {
                    downing(args[1]);
                }
                break;
            default:
                break;
        }
    }

    static void downing(String config) {
        Queue<Video> queue = new ArrayDeque<>();
        List<Video> list = JSON.parseObject(readString(config), new TypeReference<List<Video>>() {
        });
        list.forEach(video -> {
            if (video.getUrl().contains(".m3u8?")) {
                String mp4 = video.getDir() + "/" + video.getName() + ".mp4";
                if (new File(mp4).exists()) {
                    System.out.println(mp4 + "\t文件已存在，跳过。");
                } else {
                    queue.offer(video);
                }
            } else if (video.getUrl().contains(".mp4?")) {
                String mp4 = video.getDir() + "/" + video.getName() + ".mp4";
                if (new File(mp4).exists()) {
                    System.out.println(mp4 + "\t文件已存在，跳过。");
                } else {
                    queue.offer(video);
                }
            } else {
                String zip = video.getDir() + "/资料/" + video.getName();
                if (new File(zip).exists()) {
                    System.out.println(zip + "\t文件已存在，跳过。");
                } else {
                    new File(video.getDir() + "/资料/").mkdirs();
                    okDown(video.getUrl(), zip);
                }
            }
        });
        Down.go(queue);
    }

    static void getVideoInfo(User user) {
        user.courses.forEach(integer -> {
            List<Video> list = new ArrayList<>();
            Boxuegu bo = new Boxuegu(integer + "", user.studentId, user._uc_t_);
            String gradeName = MyHostURL + "video/" + bo.getGradeName();
            //打印一点信息
            System.out.println(gradeName + " : courseId : " + integer);
            if (new File(gradeName + ".json").exists()) {
                System.out.println(gradeName + ":已存在,跳过");
                return;
            }
            List<String> modules = bo.getModules();
            modules.forEach(moduleId -> {
                List<ModuleFile> files = bo.getFiles(moduleId);
                files.forEach(f -> {
                    String name = f.getFileName() + "." + f.getSuffix();
                    System.out.println("\t" + name);
                    String dir = MyHostURL + "video/" + bo.getGradeName() + File.separator + moduleId.substring(10);
                    list.add(new Video(f.getFileUrl(), dir, name));
                });
//                writeString(gradeName + ".json", JSON.toJSONString(list));
//                System.out.println(JSON.toJSONString(list));
                Module sections = bo.getSections(moduleId);
                if (sections != null) {
                    sections.getSectionItems().forEach(section -> {
                        //打印一些信息
                        System.out.println("\t" + section.getSectionName());
                        String dir = MyHostURL + "video/" + bo.getGradeName() + File.separator + moduleId.substring(10) +
                                File.separator + section.getSectionName();
                        List<PointItem> pointItems = section.getPointItems();
                        pointItems.forEach(point -> {
                            String url = bo.getM3u8Url(point.getCcVideoId(), bo.getSiteId(moduleId), bo.getVc(point.getVideoId(), moduleId));
                            //名字部分
                            String name = point.getPointName();
                            Video videoInfo = new Video(url, dir, name);
                            //打印一点信息
                            System.out.println("\t\t" + name);
                            list.add(videoInfo);
                        });
//                        System.out.println(JSON.toJSONString(list));
//                        writeString(gradeName + ".json", JSON.toJSONString(list));
                    });
                }
//                System.out.println(JSON.toJSONString(list));
//                writeString(gradeName + ".json", JSON.toJSONString(list));
            });
//            System.out.println(JSON.toJSONString(list));
            writeString(gradeName + ".json", JSON.toJSONString(list));
        });

    }

    static void okDown(String src, String name) {
        try {
            if (new File(name).exists()) {
                System.out.println(name + " : 已存在，跳过！");
                return;
            }
            System.out.println("开始下载资料 : " + name);
            URL url = new URL(src);
            InputStream in = url.openStream();
            OutputStream out = new BufferedOutputStream(new FileOutputStream(name));
            byte[] bytes = new byte[4096];
            int len;
            while ((len = in.read(bytes)) > 0) {
                out.write(bytes, 0, len);
            }
            out.close();
            in.close();
            System.out.println(name + " : 下载完毕。");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
