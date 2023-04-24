import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class GetHeroAllPic {
    public static void main(String[] args) throws IOException {
        String url = "https://pvp.qq.com/web201605/herolist.shtml";
        long t1 = System.currentTimeMillis();
        // 1.与我们要爬取数据的页面建立连接
        Connection connection = Jsoup.connect(url);
        long t2 = System.currentTimeMillis();
        System.out.println("创建连接的时间：" + (t2 - t1) + "毫秒");
        // 2.通过connection对象获取网址中的html内容，文字、String、标签
        // 在Document中它们全是文档对象模型
        Document document = connection.get();
        long t3 = System.currentTimeMillis();
        System.out.println("读取document信息所用时间:" + (t3 - t2) + "毫秒");
        // 找到第一个class为herolist clearfix的文档对象
        Element elementUl = document.selectFirst("[class=herolist clearfix]");
        // 通过ul标签找到它下面所有的li子标签，根据标签名查找
        Elements elementsLis = elementUl.select("li");
        // size仅仅用来记录总共下载的图片个数，与循环结束条件无关
        int size = 0;
        // 循环遍历获取到整个elementLis集合
        for (Element elementLi : elementsLis) {

            // 选中li标签中的a标签
            Element elementA = elementLi.selectFirst("a");
            // 获取a标签中的href属性值--->指向另一个地址，那里包含了我们要爬取的高清大图
            String href = elementA.attr("href");// 获取标签中的属性值（它这里采用的是相对路径的写法）
            // 获取a标签中的文字---留着作为我们图片的名字
            String heroName = elementA.text();// 获取a标签中的text文本值
            // 利用href抓取的相对路径，做一个字符串拼接，拼接成完整路径
            String netPath = "https://pvp.qq.com/web201605/" + href;
            // 根据新拼接的path，创建一个新的链接，因为这个新的链接才有我们的要获取的图片
            Connection newConnection = Jsoup.connect(netPath);
            // 通过新连接获取到一个新的document对象
            Document newDocument = newConnection.get();
            // 找寻大图片所在的位置---div组件 class="zk-con1 zk-con"
            Element div = newDocument.selectFirst("[class=zk-con1 zk-con]");
            // 获取div中style属性值
            String divStyle = div.attr("style");
            String backgroundUrl = divStyle.substring(divStyle.indexOf("'") + 1, divStyle.lastIndexOf("'"));
            // ------分割线-------
            //获取当前英雄所有皮肤
            Element picUl = newDocument.selectFirst("[class=pic-pf-list pic-pf-list3]");
            String allName = picUl.attr("data-imgname");
            allName = allName.replace("|","-");
            String[] preNames = allName.split("-");
            for(int i = 0;i<preNames.length;i++){
                if(preNames[i].contains("&")){
                    preNames[i] = preNames[i].substring(0,preNames[i].lastIndexOf("&"));
                }
            }
            //当前英雄皮肤总数量，url数组
            String[] urldatas = new String[preNames.length];
            int skinNum = preNames.length;
            for (int i = 0;i<skinNum;i++){
                urldatas[i] = backgroundUrl.replace("1.jpg",String.valueOf(i+1)+".jpg");
                //System.out.println(urldatas[i]);
            }


            //开始下载：
            for(int i = 0;i<preNames.length;i++){
                String lastName = heroName+"-"+preNames[i];
                System.out.println("下载：<<"+lastName+">>，图片路径：-->"+urldatas[i]);
                URL url2 = new URL("https:"+urldatas[i]);
                //根据这个url地址，创建一个输入流
                InputStream inputStream = url2.openStream();
                FileOutputStream fileOutputStream = new FileOutputStream("E:\\Edge\\JavaReptile\\王者荣耀全英雄全皮肤\\" + lastName + ".jpg");
                //需要创建一个byte数组
                byte[] b = new byte[1024];
                //读取到的数据临时存放在b字节数组内
                int count = inputStream.read(b);//count 指的是读取的有效字节个数
                while (count!=-1){
                    fileOutputStream.write(b,0,count);

                    fileOutputStream.flush();

                    count = inputStream.read(b);
                }
                fileOutputStream.close();
                inputStream.close();
                size++;//记录总共下载的图片个数
            }
        }
        long t4 = System.currentTimeMillis();
        double time = (double) (t4-t1)/1000;
        System.out.println("恭喜您已完成全部下载，共耗时："+time+"秒，下载图片"+size+"张");
    }
}