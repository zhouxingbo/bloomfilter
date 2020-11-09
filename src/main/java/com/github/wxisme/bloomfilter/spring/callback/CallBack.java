package com.github.wxisme.bloomfilter.spring.callback;

/**
 * 在调用前先传入一个函数
 * 好在合适的时候调用，以完成目标任务。
 * 这个被传入的、后又被调用的函数就称为回调函数（callback function）
 */
public interface CallBack {
 // 定义一个报告 反馈的方法
 public void baoGao(int num);
}