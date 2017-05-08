package com.dovar.screencapture;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.Client;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.RawImage;
import com.dovar.screencapture.ScreenCapture.OperateAndroid;

import java.io.File;

import static android.R.attr.label;

public class MainActivity extends AppCompatActivity {

    public static String targetNum = null;
    public static IDevice device = null;
    public Thread th = null;
    public OperateAndroid oa = null;
    public int width, height;
    public double zoom = 1;
    private Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String adbLocation = System.getProperty("com.android.screenshot.bindir");
        if (adbLocation != null && adbLocation.length() != 0) {
            adbLocation += File.separator + "adb";
        } else {
            adbLocation = "adb";
        }
        AndroidDebugBridge.init(false);
        final AndroidDebugBridge bridge = AndroidDebugBridge.createBridge(adbLocation, true);
        bridge.addClientChangeListener(new AndroidDebugBridge.IClientChangeListener() {
            @Override
            public void clientChanged(Client client, int i) {

            }
        });

        bridge.addDebugBridgeChangeListener(new AndroidDebugBridge.IDebugBridgeChangeListener() {
            @Override
            public void bridgeChanged(AndroidDebugBridge androidDebugBridge) {

            }
        });

        bridge.addDeviceChangeListener(new AndroidDebugBridge.IDeviceChangeListener() {
            @Override
            public void deviceConnected(IDevice iDevice) {
                Toast.makeText(mContext, "有新的设备已连接", Toast.LENGTH_SHORT).show();
                initWindow(bridge);
            }

            @Override
            public void deviceDisconnected(IDevice iDevice) {
                Toast.makeText(mContext, "设备已断开连接", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void deviceChanged(IDevice iDevice, int i) {
                Toast.makeText(mContext, "deviceChanged\n" + i, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void initWindow(AndroidDebugBridge bridge) {
        TextView tv_click = (TextView) findViewById(R.id.tv_click);
        TextView tv_device = (TextView) findViewById(R.id.tv_device);
        TextView tv_func = (TextView) findViewById(R.id.tv_function);
        TextView tv_zoom = (TextView) findViewById(R.id.tv_zoom);


//        int count = 0;
//        while (bridge.hasInitialDeviceList() == false) {
//            try {
//                Thread.sleep(100);
//                count++;
//            } catch (InterruptedException e) {
//            }
//
//            if (count > 100) {
//                System.err.println("Timeout getting device list!");
//
//                return;
//            }
//        }
        final IDevice[] devices = bridge.getDevices();


        LinearLayout ll_devices = (LinearLayout) findViewById(R.id.ll_devices);

        for (IDevice d : devices) {
            TextView tv_d = new TextView(this);
            tv_d.setText(d.getName());
            tv_d.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (IDevice d : devices) {
                        if (d.getSerialNumber().equals(targetNum)) {

                            device = d;
                            oa = new OperateAndroid(device);
                            // oa = OperateAndroid.getOperateAndroid(device);
                            initResolution();

                            if (!th.isAlive())
                                th.start();
                        }
                    }
                }
            });
            ll_devices.addView(tv_d);
        }

        if (devices.length < 1) {
            tv_device.setText("未找到设备");
        }

        findViewById(R.id.tv_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oa.press(OperateAndroid.HOME);
            }
        });

        findViewById(R.id.tv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oa.press(OperateAndroid.BACK);
            }
        });

        findViewById(R.id.tv_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oa.press(OperateAndroid.MENU);
            }
        });

        findViewById(R.id.tv_100).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoom = 1;
                initResolution();
                width = (int) (width * zoom);
                height = (int) (height * zoom);
            }
        });

        findViewById(R.id.tv_80).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoom = 0.8;
                initResolution();
                width = (int) (width * zoom);
                height = (int) (height * zoom);
            }
        });

        findViewById(R.id.tv_50).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoom = 0.5;
                initResolution();
                width = (int) (width * zoom);
                height = (int) (height * zoom);
            }
        });

        final ImageView iv = (ImageView) findViewById(R.id.iv);

        // 得到屏幕数据线程
        th = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    iv.setImageBitmap(getImageIcon(targetNum));
                }
            }
        });

//        label.addMouseListener(new MouseListener() {
//
//            // ���̧��
//            @Override
//            public void mouseReleased(MouseEvent e) {
//                try {
//                    oa.touchUp((int) (e.getX() / zoom), (int) (e.getY() / zoom));
////					mych.touchUp((int) (e.getX() / zoom),
////							(int) (e.getY() / zoom));
//                } catch (Exception e1) {
//                    e1.printStackTrace();
//                }
//
//                System.out.println("mouseReleased");
//            }
//
//            // ��갴��
//            @Override
//            public void mousePressed(MouseEvent e) {
//                try {
//                    oa.touchDown((int) (e.getX() / zoom),
//                            (int) (e.getY() / zoom));
//                    // mych.touchDown((int) (e.getX() / zoom),
//                    // (int) (e.getY() / zoom));
//                } catch (Exception e1) {
//                    e1.printStackTrace();
//                }
//
//                System.out.println("mousePressed");
//            }
//
//            @Override
//            public void mouseExited(MouseEvent e) {
//            }
//
//            @Override
//            public void mouseEntered(MouseEvent e) {
//            }
//
//            @Override
//            public void mouseClicked(MouseEvent e) {
//            }
//        });
//
//        label.addMouseMotionListener(new MouseMotionListener() {
//
//            @Override
//            public void mouseMoved(MouseEvent e) {
//            }
//
//            // ��갴���϶�
//            @Override
//            public void mouseDragged(MouseEvent e) {
//                try {
//                    oa.touchMove((int) (e.getX() / zoom),
//                            (int) (e.getY() / zoom));
//                    // mych.touchMove((int) (e.getX() / zoom),
//                    // (int) (e.getY() / zoom));
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//            }
//        });
//
//        label.addMouseWheelListener(new MouseWheelListener() {
//
//            // ������
//            @Override
//            public void mouseWheelMoved(MouseWheelEvent e) {
//                if (e.getWheelRotation() == 1) {
//                    oa.press("KEYCODE_DPAD_DOWN");
//                } else if (e.getWheelRotation() == -1) {
//                    oa.press("KEYCODE_DPAD_UP");
//                }
//            }
//        });
//
//        this.addKeyListener(new KeyListener() {
//
//            @Override
//            public void keyTyped(KeyEvent e) {
//
//            }
//
//            // ����̧��
//            @Override
//            public void keyReleased(KeyEvent e) {
//            }
//
//            // ���̰���
//            @Override
//            public void keyPressed(KeyEvent e) {
//                int code = e.getKeyCode();
//                switch (code) {
//
//                    case KeyEvent.VK_BACK_SPACE:
//                        oa.press("KEYCODE_DEL");
//                        break;
//                    case KeyEvent.VK_SPACE:
//                        oa.press("KEYCODE_SPACE");
//                        break;
//                    case KeyEvent.VK_DELETE:
//                        oa.press("KEYCODE_FORWARD_DEL");
//                        break;
//                    case KeyEvent.VK_UP:
//                        oa.press("KEYCODE_DPAD_UP");
//                        break;
//                    case KeyEvent.VK_DOWN:
//                        oa.press("KEYCODE_DPAD_DOWN");
//                        break;
//                    case KeyEvent.VK_LEFT:
//                        oa.press("KEYCODE_DPAD_LEFT");
//                        break;
//                    case KeyEvent.VK_RIGHT:
//                        oa.press("KEYCODE_DPAD_RIGHT");
//                        break;
//                    case KeyEvent.VK_ENTER:
//                        oa.press("KEYCODE_ENTER");
//                        break;
//                    case KeyEvent.VK_CONTROL:
//                        break;
//                    case KeyEvent.VK_ALT:
//                        break;
//                    case KeyEvent.VK_SHIFT:
//                        break;
//                    default:
//                        oa.type(e.getKeyChar());
//                }
//
//            }
//        });
    }

    public void initResolution() {
        width = 480;// oa.getScreenWidth();
        height = 800;// oa.getScreenHeight();
    }

    public Bitmap getImageIcon(String targetNum) {
        try {
            // long start = System.currentTimeMillis();
            RawImage rawImage = device.getScreenshot();
            // long end = System.currentTimeMillis();
            // System.out.println("��ȡ��Ļʱ�䣺" + (end - start) + "-����");
//            BufferedImage image = new BufferedImage(rawImage.width, rawImage.height, BufferedImage.TYPE_INT_RGB);

            Bitmap image = Bitmap.createBitmap(rawImage.width, rawImage.height, Bitmap.Config.RGB_565);
            int index = 0;
            int IndexInc = rawImage.bpp >> 3;
            for (int y = 0; y < rawImage.height; y++) {
                for (int x = 0; x < rawImage.width; x++) {
                    int value = rawImage.getARGB(index);
                    index += IndexInc;
                    image.setPixel(x, y, value);
                }
            }
            return image;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
