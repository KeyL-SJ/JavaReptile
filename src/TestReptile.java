import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class TestReptile {
    public static void main(String[] args) {
        //1.分析浏览器后面展示的html标签--URL
        try {
            long time1 = System.currentTimeMillis();//计时
            Connection connection = Jsoup.connect("https://pvp.qq.com/web201605/herolist.shtml");
            long time2 = System.currentTimeMillis();
            System.out.println("创建连接所用时间：" + (time2 - time1) + "毫秒");

            Document document = connection.get();
            long time3 = System.currentTimeMillis();
            System.out.println("读取Document信息所用的时间：" + (time3 - time2) + "毫秒");

            Element elementUl = document.selectFirst("[class=herolist clearfix]");
            Elements elementsLis = elementUl.select("li");

            int size = 0;
            for (Element elementLi : elementsLis) {
                Element elementA = elementLi.selectFirst("a");
                String href = elementA.attr("href");//获取标签中的属性值
                String heroName = elementA.text();//获取a标签中的text文本值
                //拼接完整路径
                String netPath = "https://pvp.qq.com/web201605/" + href;
                Connection newConnection = Jsoup.connect(netPath);
                //通过新连接获取到一个新的document对象
                Document newDocument = newConnection.get();
                //寻找大图片所在的位置 ——— div组件 class = "zk-con1 zk-con"
                Element div = newDocument.selectFirst("[class=zk-con1 zk-con]");
                String divStyle = div.attr("style");
                String backgroundUrl = divStyle.substring(divStyle.indexOf("'") + 1, divStyle.lastIndexOf("'"));

                //获取当前英雄的所有皮肤
                Element picUl = newDocument.selectFirst("[class=pic-pf-list pic-pf-list3]");
                String allName = picUl.attr("data-imgname");
                allName = allName.replace("|", "-");
                String[] preNames = allName.split("-");
                for (int i = 0; i < preNames.length; i++) {
                    if (preNames[i].contains("&")) {
                        preNames[i] = preNames[i].substring(0, preNames[i].lastIndexOf("&"));
                    }
                }
                //当前英雄全部皮肤
                String[] urldatas = new String[preNames.length];
                int skinNum = preNames.length;
                for (int i = 0; i < skinNum; i++) {
                    urldatas[i] = backgroundUrl.replace("1.jpg", String.valueOf(i + 1) + ".jpg");
                    //System.out.println(urldatas[i]);
                }
                //2.下载（I/O)
                for (int i = 0; i < preNames.length; i++) {
                    String lastName = heroName + "-" + preNames[i];
                    System.out.print("正在下载：《"+lastName+"》");
                    URL url = new URL("https:" + urldatas[i]);
                    InputStream inputStream = url.openStream();
                    FileOutputStream fos = new FileOutputStream("E:\\Edge\\JavaReptile\\王者荣耀全英雄全皮肤\\" + lastName + ".jpg");
                    byte[] b = new byte[1024];
                    int count = inputStream.read(b);
                    while (count != -1) {
                        fos.write(b, 0, count);
                        fos.flush();
                        count = inputStream.read(b);
                    }
                    inputStream.close();
                    fos.close();
                    size++;
                    System.out.println("\t下载完成");
                }
            }
            long time4 = System.currentTimeMillis();
            double time = (double) (time4 - time1) / 1000;
            System.out.println("下载完成，共耗时："+time+"秒,共下载图片"+size+"张");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
