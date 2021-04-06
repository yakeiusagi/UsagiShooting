/*BGM,SE*/
import processing.sound.*;
class Music{
  
  SoundFile bgm1;    
  SoundFile hitPlayer;  //被弾時SE
  SoundFile defeteEnemy;  //敵機撃破SE
  SoundFile graze;  //グレイズSE
  SoundFile extend;  //エクステンドSE
  
  Music(PApplet sketch){
    
    bgm1 = new SoundFile(sketch,"Hide-and-seek.mp3");
    hitPlayer = new SoundFile(sketch,"se_maoudamashii_magical19.mp3");
    defeteEnemy = new SoundFile(sketch,"se_maoudamashii_battle09.mp3");
    graze = new SoundFile(sketch,"firecracker1.mp3");
    extend = new SoundFile(sketch,"magic-statusup1.mp3");
  }
  
  void playBGM(){
    ///bgm1.amp(0.0);
    if(!bgm1.isPlaying()){
      bgm1.loop();
    }
  }
  
  void playHitPlayer(){
    hitPlayer.amp(0.3);
    hitPlayer.play();
  }

  void playDefeteEnemy(){
    defeteEnemy.amp(0.7);
    defeteEnemy.play();
  }

  void playGraze(){
    if(!graze.isPlaying()){
      //graze.amp(2.5);
      graze.play();
    }
  }

  void playExtend(){
    if(!extend.isPlaying()){
      //graze.amp(2.5);
      extend.play();
    }
  }
  
}
