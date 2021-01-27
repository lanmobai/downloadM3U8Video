🏆🍽 
额, 其实这个还可以用, 但是里面有些小问题, 需要大家自行处理, 切合学习研究之用.

此仅为技术研究之用, 请勿用作倒卖侵权的工具. 尊重产权, 敬畏法律. 之所以不更新, 就是为避免可能的麻烦, 毕竟这并不是斗罗大陆, 隔一个星期就免费了🎨.



# 停止维护.
2021年1月5日15时36分40秒


# boxuegu
博学谷视频下载

使用了 [fastjson](https://github.com/alibaba/fastjson) 、[m3u8Download](https://github.com/qq494257084/m3u8Download) 、[jsoup](https://github.com/jhy/jsoup)

# 使用方法

1. 使用chrome浏览器，打开博学谷官网并登录帐号

2. 打开我的课程
   
   ![image-20200710123133494](img/image-20200710123133494.png)
   
3. 按F12，打开DevTools，选择Application选项
   
   ![image-20200710123225395](img/image-20200710123225395.png)
   
4. 选择cookie字段填写配置文件
   
   ![image-20200710123500718](img/image-20200710123500718.png)
   
5. ```
   boxuegu.bat [custom.json]   //根据配置文件获取视频信息，并记录保存到data/目录中，默认配置为user.json
   boxuegu.bat d video.json    //根据 video.json 下载视频到video/目录中
   ```

   

