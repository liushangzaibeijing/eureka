package com.xiu.hystrix.rxjava;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 读者
 */

public class Reader {

    private Observer reader;

    public Reader(){

        reader = new Observer<String>() {
            public Disposable disposable;

            /**
             * 订阅时间处理
             * @param disposable
             */
            @Override
            public void onSubscribe(Disposable disposable) {
               this.disposable = disposable;

               System.out.println("处理订阅操作");
            }

            /**
             * 获取发送信息
             * @param value
             */
            @Override
            public void onNext(String value) {
                System.out.println("获取最新小说："+value);

            }

            /**
             * 获取错误信息
             * @param throwable
             */
            @Override
            public void onError(Throwable throwable) {
                System.out.println("处理错误信息");
            }

            /**
             * 监听完成
             */
            @Override
            public void onComplete() {
                System.out.println("信息获取完成");
            }
        };
    }


    public Observer getReader() {
        return reader;
    }

    public void setReader(Observer reader) {
        this.reader = reader;
    }
}
