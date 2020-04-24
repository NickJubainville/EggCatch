package njubainville1.nait.ca.eggcatch;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

/**
 * Created by Nick on 2019-04-13.
 */

public class SoundPlayer {
    private AudioAttributes audioAttributes;
    final int SOUND_POOL_MAX = 3;

    private static SoundPool soundPool;
    private static int hitOrangeSound;
    private static int hitBlackSound;
    private static int hitPinkSound;

    public SoundPlayer(Context context)
    {
        //SoundPool is deprecated in API 21 (Lollipop)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build();

            soundPool = new SoundPool.Builder().setAudioAttributes(audioAttributes)
                    .setMaxStreams(SOUND_POOL_MAX).build();
        }
        else
        {
            //SoundPool (int maxStreams, int streamType, int srcQuality)
            soundPool = new SoundPool(SOUND_POOL_MAX, AudioManager.STREAM_MUSIC, 0);
        }

        hitOrangeSound = soundPool.load(context, R.raw.orange, 1);
        hitPinkSound = soundPool.load(context, R.raw.pink, 1);
        hitBlackSound = soundPool.load(context, R.raw.black, 1);
    }
    public void playHitPinkSound()
    {
        //play(int SoundID, float leftVolume, float rightVolume, int priority, int loop, float rate)
        soundPool.play(hitPinkSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }
    public void playHitOrangeSound()
    {
        soundPool.play(hitOrangeSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }
    public void playHitBlackSound()
    {
        soundPool.play(hitBlackSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }
}

