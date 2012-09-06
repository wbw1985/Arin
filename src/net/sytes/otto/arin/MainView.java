package net.sytes.otto.arin;

import java.util.Random;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

class MainView extends SurfaceView
implements SurfaceHolder.Callback, Runnable {

	Paint paint = null;			// 描画用
    Thread mainLoop = null;		// スレッド
    private int x = 10,y = 10;	// イラストの座標
    private int vx = 5;			// イラストの移動量
    private MediaPlayer mp[];	// 音声再生
    private Bitmap image;		// イラスト
    private Bitmap button;		// ボタン
    private int count = 0;		// ボタンのクリック回数

    public MainView(Context context) {
        super(context);
        // SurfaceView描画に用いるコールバックを登録する。
        getHolder().addCallback(this);

        // 描画用の準備
        paint = new Paint();
        paint.setColor(Color.WHITE);

        // スレッド開始
        mainLoop = new Thread(this);
        mainLoop.start();

        // リソースからビットマップを取り出す
        Resources r = getResources();
        image = BitmapFactory.decodeResource(r, R.drawable.image1);
        button = BitmapFactory.decodeResource(r, R.drawable.button);

        // メディアプレイヤーを作成
        mp = new MediaPlayer[6];
        mp[0]=MediaPlayer.create(context,R.raw.sample0);
        mp[1]=MediaPlayer.create(context,R.raw.sample1);
        mp[2]=MediaPlayer.create(context,R.raw.sample2);
        mp[3]=MediaPlayer.create(context,R.raw.sample3);
        mp[4]=MediaPlayer.create(context,R.raw.sample4);
        mp[5]=MediaPlayer.create(context,R.raw.sample5);
    }

    // 音声を再生中かどうか
    private boolean isPlaying(){
    	for(int i=0;i<mp.length;i++){
    		if(mp[i].isPlaying()){
    			return true;
    		}
    	}
    	return false;
    }

    // 音声を再生する
    private void start(){
    	int x = new Random().nextInt(mp.length);
    	mp[x].start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // 背景を変更
        Canvas canvas = holder.lockCanvas();
        canvas.drawColor(Color.MAGENTA);
        holder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    	mainLoop = null;
    }

    // タッチイベント
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 音楽が再生中でなければ再生する
        if(!this.isPlaying()){
        	this.start();
        	count++;
        }
        return true;
    }

    @Override
    public void run() {
        // Runnableインターフェースをimplementsしているので、runメソッドを実装する
        // これは、Threadクラスのコンストラクタに渡すために用いる。
        while (true) {
            Canvas canvas = getHolder().lockCanvas();
            if (canvas != null)
            {
            	// 背景色を設定
                canvas.drawColor(Color.MAGENTA);

                // グリッドを描画
                Paint pa = new Paint();
                pa.setColor(Color.argb(75, 255, 255, 255));
                pa.setStrokeWidth(1);
                for (int y = 0; y < 800; y = y + 10) {
                    canvas.drawLine(0, y, 479, y, pa);
                }
                for (int x = 0; x < 480; x = x + 10) {
                    canvas.drawLine(x, 0, x, 799, pa);
                }

                // イラストとボタンを描画
                canvas.drawBitmap(image, x, y, paint);
                canvas.drawBitmap(button, getWidth()/2, getHeight()*2/3,paint);

                // 押された回数を描画する
                Paint p = new Paint();
                p.setColor(Color.BLACK);
                p.setTextSize(50);
                String s = String.format("%1$04dあーりんだよぉ", count);
                canvas.drawText(s, 0, 400, p);
                getHolder().unlockCanvasAndPost(canvas);

                // イラストの移動
                x += vx;
                if (x < 0 || getWidth() < x+image.getHeight())  vx *= -1;
            }
        }
    }
}