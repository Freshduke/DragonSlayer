package huawei;

import huawei.exam.Area;
import huawei.exam.ExamCmd;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Label;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.huawei.exam.Command;
import com.huawei.exam.ExamSocketServer;

/**
 * 主执行类
 *
 * 考生不得修改，亦无须关注
 */
public class DragonSlayerMain
{
    public static void main(String[] args) throws IOException {
		/**
		 * 启动Socket服务侦听5555端口，从Socket获取命令，会丢给Command类的command函数执行
		 * Command类的command函数已经实现了从Socket接收到字符串后的解析与分发 考生只需要实现DragonSlayerImpl类的各命令接口即可。
		 **/

		Command cmd = new ExamCmd(new DragonSlayerImpl());
		ExamSocketServer ess = new ExamSocketServer(cmd);
		ess.start();
	}
}
