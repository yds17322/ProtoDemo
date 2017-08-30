package yds.yds.yds.protodemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.protobuf.InvalidProtocolBufferException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import yds.yds.yds.proto.Demo;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Demo.Person.PhoneNumber phoneNumber1 = Demo.Person.PhoneNumber.newBuilder()
                .setType(Demo.Person.PhoneType.WORK)
                .setNumber("123456")
                .build();

        Demo.Person.PhoneNumber phoneNumber2 = Demo.Person.PhoneNumber.newBuilder()
                .setType(Demo.Person.PhoneType.HOME)
                .setNumber("654321")
                .build();

        Demo.Person person = Demo.Person.newBuilder()
                .setName("yds")
                .setId(1)
                .setEmail("yds@12345.com")
                .addPhone(0, phoneNumber1)
                .addPhone(1, phoneNumber2)
                .build();
        // 保存
        save(person);
        // 读取
        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parseFrom(read());
            }
        });

        // 直接转为流
        Log.e(TAG, Arrays.toString(person.toByteArray()));
        // 流读取
        parseFrom(person.toByteArray());

        // 通过OutputStream转为流
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            person.writeTo(output);
            // 将消息序列化 并写入 输出流(此处用 ByteArrayOutputStream 代替)
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e(TAG, Arrays.toString(output.toByteArray()));
        // 流读取
        parseFrom(person.toByteArray());

    }

    private void parseFrom(byte[] read) {
        try {
            Demo.Person person = Demo.Person.parseFrom(read);
            Log.d(TAG, person.getEmail());
            Log.d(TAG, person.getName());
            Log.d(TAG, person.getId() + "");
            Log.d(TAG, person.getPhone(0).getNumber());
            Log.d(TAG, person.getPhone(0).getType() == Demo.Person.PhoneType.WORK ? "WORK" : "other");
            Log.d(TAG, person.getPhone(1).getNumber());
            Log.d(TAG, person.getPhone(1).getType() == Demo.Person.PhoneType.HOME ? "HOME" : "other");
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    void save(Demo.Person person) {
        File file = new File("/sdcard/person");
        Log.d(TAG, "file ---> " + file.getPath());
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            person.writeTo(outputStream);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    byte[] read() {
        File file = new File("/sdcard/person");
        try {
            FileInputStream inputStream = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] data = new byte[1024];
            int len = -1;
            while ((len = inputStream.read(data)) != -1) {
                out.write(data, 0, len);
                out.flush();
            }
            return out.toByteArray();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return new byte[0];
    }
}
