import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.sound.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class UsagiShooting extends PApplet {

/** ★★★うさぎシューティング★★★ **/

//モード
//int mode = Const.MODE_DEV;
int mode = Const.MODE_PRO;

//シーン番号
int sceneNo = Const.SCENE_NO_START;

//オブジェクト
Opening op;
Title title;
Player player;
ArrayList<Stage> stageList;
Music music;
Image img;
Scenario scenario;
int bgCol;
GameOver gameOver;

public void setup(){
  
  
  PFont font = createFont("Meiryo", 40);
  textFont(font);
  
  op = new Opening();
  title = new Title();
  gameOver = new GameOver();
  //music = new Music(this);
  //img = new Image();
  scenario = new Scenario();
  bgCol = color(0);
  
  /*自機生成*/
  player = new Player(width/2,height*4/5);
  
  /*ステージリスト生成*/
  stageList = new ArrayList<Stage>();
  stageList.add(new Stage1());
  stageList.add(new Stage2());
  
  /*各種初期化*/
  title.init();

}

public void draw(){
  background(bgCol);
  
  switch(sceneNo){
  
     case Const.SCENE_NO_START:
       /*重そうな処理は先にローディング画面を出してから行う(あまり効果なし?）*/
       fill(255);
       textSize(30);
       text("now loading...",50,370);       
       music = new Music(this);
       img = new Image();
       sceneNo = Const.SCENE_NO_TITLE;
       
     case Const.SCENE_NO_TITLE:
       /*タイトル*/
       drawTitle();
       break;
    
     case Const.SCENE_NO_OPENING:
       /*オープニング*/
       int count = op.drawOpening();
       if(count == 0){
         sceneNo = Const.SCENE_NO_STAGE1;
       }
       break;

     case Const.SCENE_NO_STAGE1:
       /*ステージ1(道中)*/
       music.playBGM();
       Stage stage1 = stageList.get(0);
       execGame(stage1);
       break;

     case Const.SCENE_NO_PRE_BOSS:
       /*ボス前シーン*/
       drawStoryPreBoss();
       break;

     case Const.SCENE_NO_BOSS:
       /*ボス戦*/
       music.playBGM();
       Stage stage2 = stageList.get(1);
       execGame(stage2);       
       break;

     case Const.SCENE_NO_CLEAR:
       /*クリア*/
       drawResult();
       break;
       
     case Const.SCENE_NO_GAMEOVER:
       /*ゲームオーバー*/
       gameOver.enableCountFlg();
       gameOver.drawGameOver();
       break;
  }
}

public void keyPressed(){
  /* 自機移動方向制御 */
  player.pushDirect();
  /* 自機ステータス制御 */
  player.updateStatusByKey(key,Const.KEY_FLG_PRESS);  
}

public void keyReleased(){
  /* 自機移動方向制御 */
  player.popDirect();
  /* 自機ステータス制御 */
  player.updateStatusByKey(key,Const.KEY_FLG_RELEASE);
  /* 会話シーンを進める */
  if(sceneNo == Const.SCENE_NO_PRE_BOSS && (key==ENTER || key==RETURN)){
    scenario.moveToNext();
  }
}

//**ゲームを実行
public void execGame(Stage stage){
  
  /*上部情報画面を描画*/
  drawInfo();
  
  /*未初期化の場合、ステージ初期化*/
  if(!stage.getInitFlg()){
    stage.init();
  }
  
  /*チャプターリストを読み込み*/
  ArrayList<Chapter> chapterList = stage.getChapterList();
  
  /*チャプターを実行準備*/
  Chapter ch = chapterList.get(0);
  
  //チャプター終了判定
  if(ch.isChapterEnd()){
    
    //println("chapter end");

    //終了したチャプターはリストから削除
    chapterList.remove(0);
   
    //全てのチャプターが終了した場合は次のシーンに進む
    if(chapterList.size() == 0){
      //println("全チャプター終了");
      sceneNo += 1;
      if(sceneNo != Const.SCENE_NO_CLEAR){ player.extend(); }
      return;
    }
    
    //次のチャプターの敵インスタンスの準備
    ch = chapterList.get(0);
    ch.createEnemy(player);
    
  }
  /*自機を描画*/
  player.draw();
  
  /*チャプターを実行*/
  ch.exec(player);

  /*ゲームオーバー判定*/
  if(mode == Const.MODE_PRO && player.getZanki() <= -1){
    sceneNo = Const.SCENE_NO_GAMEOVER;
  }
 
}

//**タイトル画面を再生
public void drawTitle(){
  title.drawTitle();  
  if(keyPressed && (key==ENTER || key==RETURN)){
    sceneNo = Const.SCENE_NO_OPENING;
  }
}

//**リトライ処理
public void retry(){
  op.resetCount();
  stageList.clear();
  stageList.add(new Stage1());
  stageList.add(new Stage2());
  player.init();
  Score.init();
  sceneNo = Const.SCENE_NO_OPENING;
}

//**リザルト画面を再生
public void drawResult(){
  fill(255);
  textSize(30);
  text("score:"+Score.getScore(),100,250);
  text("clear bonus:"+Const.SCORE_CLEAR + "*" + player.getZanki()+" (life remain)",100,320);
  textSize(45);
  int totalScore = Score.getScore()+ Score.getClearBonus(player.getZanki());
  text("total score:"+totalScore,100,420);
  
  //リトライ
  if(keyPressed){
    //retry();
  }
}

//**ボス前会話シーンを再生
public void drawStoryPreBoss(){
  
  /*上部情報画面描画*/
  drawInfo();
  
  /*自機を描画*/
  player.draw();
  
  /*ボスを描画*/
  img.drawBossImage(new PVector(width/2,100));
  
  /*会話を再生*/
  scenario.drawStoryPreBoss();
  
  /* 終了 */
  if(scenario.isFinishScenario()){
    sceneNo = Const.SCENE_NO_BOSS;
  } 

}

//**上部情報画面を描画
public void drawInfo(){
  noStroke();
  fill(127);
  rect(0,0,width,Const.HEIGHT_INFO);
  //残機数
  fill(255);
  for(int i=0; i<player.getZanki(); i++){
    rect(375 + (i*30),30,10,10);
  }
  //スコア
  textSize(20);
  text("score:"+str(Score.getScore()),30,35);
  //グレイズ
  textSize(20);
  text("graze:"+str(Score.getGraze()),200,35);
  
}

/* 弾クラス(基底) */
abstract class Bullet{
  protected PVector location;  //位置ベクトル
  protected PVector velocity;  //速度ベクトル
  protected PVector gravity;   //重力ベクトル
  protected int col;        //弾幕色
  protected float range;    //弾の当たり判定半径
  protected int boundNum;    //バウンド回数
  
  //** コンストラクタ
  Bullet(PVector location,PVector velocity,PVector gravity,int col){
    this.location = location;
    this.velocity = velocity;
    if(gravity != null){
      this.gravity = gravity;
    }else{
      this.gravity = new PVector(0.0f,0.0f);  //重力不使用
    }
    this.col = col;
    this.range = 0.0f;
    this.boundNum = 0;
  }
  
  //** 弾を描画
  public abstract void draw();

  //** 弾の位置を更新
  public void updateLocation(){
    location.add(velocity); 
    location.add(gravity);
  }
  
  //**自機への当たり判定
  public boolean isHitToPlayer(Player player){
    float Lx = location.x - range;  //弾当たり左端
    float Rx = location.x + range;  //弾当たり右端
    float Uy = location.y - range;  //弾当たり上端
    float Dy = location.y + range;  //弾当たり下端
    
    float playerX = player.getLocation().x;
    float playerY = player.getLocation().y;
    float PLx = playerX - Const.RANGE_HIT_PLAYER;  //自機左端
    float PRx = playerX + Const.RANGE_HIT_PLAYER;  //自機右端
    float PUy = playerY - Const.RANGE_HIT_PLAYER;  //自機上端
    float PDy = playerY + Const.RANGE_HIT_PLAYER;  //自機下端
    
    if(Lx < PRx && Rx > PLx){
      if(Uy < PDy && Dy > PUy){
        return true;
      }
    }
    
    return false;
    
  }
  
  //**自機へのグレイズ判定
  public abstract boolean isGrazeToPlayer(Player player);
  
  public boolean isGrazeToPlayerCommon(Player player,float grazeRange){
    float Lx = location.x - grazeRange;  //弾グレイズ左端
    float Rx = location.x + grazeRange;  //弾グレイズ右端
    float Uy = location.y - grazeRange;  //弾グレイズ上端
    float Dy = location.y + grazeRange;  //弾グレイズ下端
    
    float playerX = player.getLocation().x;
    float playerY = player.getLocation().y;
    float PLx = playerX - Const.RANGE_HIT_PLAYER;  //自機左端
    float PRx = playerX + Const.RANGE_HIT_PLAYER;  //自機右端
    float PUy = playerY - Const.RANGE_HIT_PLAYER;  //自機上端
    float PDy = playerY + Const.RANGE_HIT_PLAYER;  //自機下端
    
    if(Lx < PRx && Rx > PLx){
      if(Uy < PDy && Dy > PUy){
        return true;
      }
    }
    
    return false;
    
  }
  
  //**弾が画面外にはみ出しているか判定
  public boolean isOutOfScreen(){
    if(location.x < 0 || location.x > width){
      return true;
    }
    if(location.y < 0 || location.y > height){
      return true;
    }
    return false;
  }

  //**弾のバウンド回数カウントアップ
  public void countBoundNum(){
    boundNum += 1;
  }  

  /*getter,setter*/
  public PVector getLocation(){
    return location;
  }
  public PVector getVelocity(){
    return velocity;
  }  
  public void setVelocity(PVector velocity){
    this.velocity = velocity;
  }
  public int getBoundNum(){
    return boundNum;
  }
  
}

/*------------------------------------------------------------*/
/* 小弾クラス */
class SmallBullet extends Bullet{
  
  //** コンストラクタ
  SmallBullet(PVector location,PVector velocity,PVector gravity,int col){
    super(location,velocity,gravity,col);
    this.range = Const.SMALLBULLET_RANGE;
  }
  
  //** 弾を描画
  public void draw(){
    noStroke();
    //グラデーション付き
    fill(col,10);
    ellipse(location.x,location.y,Const.SMALLBULLET_DIAMETER+7,Const.SMALLBULLET_DIAMETER+7);
    fill(col,30);
    ellipse(location.x,location.y,Const.SMALLBULLET_DIAMETER+5,Const.SMALLBULLET_DIAMETER+5);
    fill(col,60);
    ellipse(location.x,location.y,Const.SMALLBULLET_DIAMETER+3,Const.SMALLBULLET_DIAMETER+3);
    fill(col);
    ellipse(location.x,location.y,Const.SMALLBULLET_DIAMETER,Const.SMALLBULLET_DIAMETER);
    fill(255,60);
    ellipse(location.x,location.y,Const.SMALLBULLET_DIAMETER/3,Const.SMALLBULLET_DIAMETER/3);
  }
  
  //** グレイズ判定
  public boolean isGrazeToPlayer(Player player){
    return isGrazeToPlayerCommon(player,Const.SMALLBULLET_GRAZE);
  }
  
}

/*------------------------------------------------------------*/
/* 大弾クラス */
class LargeBullet extends Bullet{
  
  //** コンストラクタ
  LargeBullet(PVector location,PVector velocity,PVector gravity,int col){
    super(location,velocity,gravity,col);
    this.range = Const.LARGEBULLET_RANGE;
  }
  
  //** 弾を描画
  public void draw(){
    noStroke();
    fill(col,10);
    ellipse(location.x,location.y,Const.LARGEBULLET_DIAMETER+20,Const.LARGEBULLET_DIAMETER+20);
    fill(col,30);
    ellipse(location.x,location.y,Const.LARGEBULLET_DIAMETER+15,Const.LARGEBULLET_DIAMETER+15);
    fill(col,60);
    ellipse(location.x,location.y,Const.LARGEBULLET_DIAMETER+10,Const.LARGEBULLET_DIAMETER+10);
    fill(col,70);
    ellipse(location.x,location.y,Const.LARGEBULLET_DIAMETER+5,Const.LARGEBULLET_DIAMETER+5);
    fill(col);
    ellipse(location.x,location.y,Const.LARGEBULLET_DIAMETER,Const.LARGEBULLET_DIAMETER);
  }

  //** グレイズ判定
  public boolean isGrazeToPlayer(Player player){
    return isGrazeToPlayerCommon(player,Const.LARGEBULLET_GRAZE);
  }
  
}

/*------------------------------------------------------------*/
/* 三角弾クラス */
/* メモ:描画はイラストにして、実際の当たり判定は円形*/
class TriangleBullet extends Bullet{

  //** コンストラクタ
  TriangleBullet(PVector location,PVector velocity,PVector gravity,int col){
    super(location,velocity,gravity,col);
    this.range = 10;
  }
  
  //** 弾を描画
  public void draw(){
    fill(col);
  }
  
  //** グレイズ判定
  public boolean isGrazeToPlayer(Player player){
    //return isGrazeToPlayerCommon(player,Const.LARGEBULLET_GRAZE);
    return false;
  }

}
/* 弾幕クラス(基底) */
abstract class BulletHell{
  
  protected ArrayList<Bullet> bulletList;  //弾リスト
  protected ArrayList<Laser> laserList;  //レーザーリスト
  protected String bulletType;  //弾種
  
  //**コンストラクタ
  BulletHell(){
    bulletList = new ArrayList<Bullet>();
    laserList = new ArrayList<Laser>();
  }
  
  //**弾幕を描画
  public abstract void draw(int status);
  
  //**描画処理
  public void drawBulletHell(){
    
    //弾
    if(bulletList.size() != 0){
      for(Bullet b :bulletList){
        b.updateLocation();
        b.draw();
      }
    }
    
    //レーザー
    if(laserList.size() != 0){
      for(Laser l : laserList){
        l.updateLocation();
        l.draw();
      }
    }
    
  }
  
  //**自機への当たり判定
  public boolean isHitToPlayer(Player player){
    //弾
    for(Bullet b : bulletList){
      if(b.isHitToPlayer(player)){
        return true;
      }
    }
    //レーザー
    for(Laser l : laserList){
      if(l.isHitToPlayer(player)){
        return true;
      }
    }    
    return false;
  }
  
  //**自機へのグレイズ回数
  public int calcGrazeNum(Player player){
    int grazeNum = 0;
    for(Bullet b : bulletList){
      if(b.isGrazeToPlayer(player)){
        grazeNum += 1;
      }
    }
    return grazeNum;
  }
  
  //**画面外に出た弾を消去
  public void deleteBullet(){
    for(int i=0; i<bulletList.size(); i++){
      if(bulletList.get(i).isOutOfScreen()){
        bulletList.remove(i);
      }
    }
    //for(int i=0; i<laserList.size(); i++){
    //  if(laserList.get(i).isOutOfScreen()){
    //    laserList.remove(i);
    //  }
    //}
  }
  
  //**弾幕を全消去
  public void deleteAllBullet(){
    bulletList.clear();
    laserList.clear();
  }

  /*getter,setter*/
  public ArrayList<Bullet> getBulletList(){
    return bulletList;
  }
  public ArrayList<Laser> getLaserList(){
    return laserList;
  }
  
}
/*------------------------------------------------------------*/
/* 弾タイプ */
/*------------------------------------------------------------*/
/* 全方位弾幕 */
class AllRoundBullletHell extends BulletHell{
  
  private int num;  //弾数
  PVector enemyLocation;  //敵機位置
  
  AllRoundBullletHell(int num,String bulletType,PVector enemyLocation){
    super();
    this.num = num;
    this.bulletType = bulletType;
    this.enemyLocation = enemyLocation;
  }
  
  //**弾幕を描画
  public void draw(int status){
      
    float angle;         //弾幕の角度
    float velocity = 3;  //弾幕の速度
    int col = color(98,216,198);
        
    //1秒毎に弾幕を生成する(敵機がアクティブの場合)
    if(status == Const.STATUS_ENEMY_ACTIVE && frameCount%60 == 0){
      for(int i=0; i<num;i++){
        angle = 2*PI/num * (i+1);
        switch(bulletType){
          case Const.BULLET_TYPE_SMALL:
            bulletList.add(new SmallBullet(new PVector(enemyLocation.x,enemyLocation.y)
                                            ,new PVector(cos(angle)*velocity,sin(angle)*velocity)
                                            ,null,col));
            break;
          case Const.BULLET_TYPE_LARGE:
            bulletList.add(new LargeBullet(new PVector(enemyLocation.x,enemyLocation.y)
                                            ,new PVector(cos(angle)*velocity,sin(angle)*velocity)
                                            ,null,col));
            break;
        }
      }
    }
     
    //描画
    drawBulletHell();
    
    //画面外に出た弾のインスタンス削除
    deleteBullet();

  }
  
}

/*------------------------------------------------------------*/
/* 自機狙い弾幕 */
class TargetingBulletHell extends BulletHell{
    PVector enemyLocation;  //敵機位置
    PVector playerLocation;  //自機位置
    int col = color(98,216,198);
  TargetingBulletHell(PVector enemyLocation,PVector playerLocation){
    super();
    this.enemyLocation = enemyLocation;
    this.playerLocation = playerLocation;
  }
   
  //**弾幕を描画
  public void draw(int status){
    
    float angle;         //弾幕の角度
    float velocity = 6;  //弾幕の速度
    
    float enemyY;
    float enemyX;
    float playerY;
    float playerX;
    
    //一定間隔で弾インスタンスを生成する(敵機がアクティブの場合)
    if(status == Const.STATUS_ENEMY_ACTIVE && frameCount%20 == 0){
      enemyY = enemyLocation.y;
      enemyX = enemyLocation.x;
      playerY = playerLocation.y;
      playerX = playerLocation.x;
      angle = atan2(playerY - enemyY,playerX - enemyX);  
      //小弾：始点位置は敵機の座標、向きは自機の位置
      bulletList.add(new SmallBullet(new PVector(enemyLocation.x,enemyLocation.y)
                      ,new PVector(cos(angle)*velocity,sin(angle)*velocity)
                      ,null,col));
    }
    
    //描画
    drawBulletHell();
    
    //画面外に出た弾のインスタンス削除
    deleteBullet();

  }

}

/*------------------------------------------------------------*/
/* ランダム発射弾幕 */
class RandomBulletHell extends BulletHell{
  PVector enemyLocation;  //敵機位置
  int interval;  //発射間隔
  int col = color(98,216,198);

  RandomBulletHell(PVector enemyLocation,String bulletType,int interval){
    super();
    this.enemyLocation = enemyLocation;
    this.bulletType = bulletType;
    this.interval = interval;
  }
  
  //**弾幕を描画
  public void draw(int status){
    
    float angle;         //弾幕の角度
    float velocity = 2.5f;  //弾幕の速度
    
    //一定間隔で弾幕生成(敵機がアクティブの場合)
    if(status == Const.STATUS_ENEMY_ACTIVE && frameCount%interval == 0){
      angle = random(2*PI);
      switch(bulletType){
        case Const.BULLET_TYPE_SMALL :
          bulletList.add(new SmallBullet(new PVector(enemyLocation.x,enemyLocation.y)
                          ,new PVector(cos(angle)*velocity,sin(angle)*velocity)
                          ,new PVector(0.0f,1.0f),col));
          break;
        case Const.BULLET_TYPE_LARGE :
          bulletList.add(new LargeBullet(new PVector(enemyLocation.x,enemyLocation.y)
                          ,new PVector(cos(angle)*velocity,sin(angle)*velocity)
                          ,new PVector(0.0f,1.0f),col));
          break;
      }
    }
    
    //描画
    drawBulletHell();
    
    //画面外に出た弾のインスタンス削除
    deleteBullet();
    
  }
 
}

/*------------------------------------------------------------*/
/* 落下弾幕 */
class FallBulletHell extends BulletHell{
  int col = color(98,216,198);
  FallBulletHell(){
    super();
  }
  
  //**弾幕を描画
  public void draw(int status){
     if(status == Const.STATUS_ENEMY_ACTIVE && frameCount%10 == 0){
       bulletList.add(new LargeBullet(new PVector(random(width),0),new PVector(0,0),new PVector(0,2),col));
     }
    //描画
    drawBulletHell();
    //画面外に出た弾のインスタンス削除
    deleteBullet();   
  }
  
}

/*------------------------------------------------------------*/
/* レーザー型弾幕 */
class LaserLikeBulletHell extends BulletHell{
  PVector enemyLocation;  //敵機位置
  float angle;  //発射角度
  float vector;  //速度
  int col = color(98,216,198);
  
  LaserLikeBulletHell(PVector enemyLocation,float angle){
    super(); 
    this.enemyLocation = enemyLocation;
    this.angle = angle;
    this.vector = 4.0f;
  }
  
  //**弾幕を描画
  public void draw(int status){
    if(status == Const.STATUS_ENEMY_ACTIVE && frameCount%4 == 0){
      bulletList.add(new SmallBullet(new PVector(enemyLocation.x,enemyLocation.y),new PVector(cos(angle)*vector,sin(angle)*vector),null,col));
    }
    //描画
    drawBulletHell();
    //画面外に出た弾のインスタンス削除
    deleteBullet();   
  }
  
}

/*------------------------------------------------------------*/
/* らせん放射型弾幕 */
class HelixBulletHell extends BulletHell{
  PVector enemyLocation;
  float angle = PI/2;  //開始角度
  float angleAdd;      //角度増分
  float vector;
  
  HelixBulletHell(PVector enemyLocation,String bulletType,float angleAdd){
    super();
    this.bulletType = bulletType;
    this.enemyLocation = enemyLocation;
    this.angleAdd = angleAdd;
    vector = 4;
  }
  
  //**弾幕を描画
  public void draw(int status){
    if(status == Const.STATUS_ENEMY_ACTIVE){
      angle += angleAdd;
      if(angle>2*PI){ angle -= 2*PI; }
      //println("らせん angle = " + degrees(angle));
      switch(bulletType){
        case Const.BULLET_TYPE_SMALL:
          bulletList.add(new SmallBullet(new PVector(enemyLocation.x,enemyLocation.y),new PVector(cos(angle)*vector,sin(angle)*vector),null,color(98,216,198)));
          break;
        case Const.BULLET_TYPE_LARGE:
          bulletList.add(new LargeBullet(new PVector(enemyLocation.x,enemyLocation.y),new PVector(cos(angle)*vector,sin(angle)*vector),null,color(98,216,198)));
          break;
      }
    }
    drawBulletHell();
    deleteBullet();
  }

}

/*------------------------------------------------------------*/
/* 複数らせん放射型弾幕 */
class MultiHelixBulletHell extends BulletHell{
  PVector enemyLocation;
  int lineNum;    //らせん放射数
  int interval;   //発射間隔
  float angle;      //開始角度
  float angleAdd;  //角度増分
  boolean turnFlg;  //転換フラグ
  int turnInterval;  //転換間隔
  float vector;
  
  MultiHelixBulletHell(PVector enemyLocation,String bulletType,float angleAdd,int interval,int lineNum,boolean turnFlg,int turnInterval){
    super();
    this.bulletType = bulletType;
    this.enemyLocation = enemyLocation;
    this.angleAdd = angleAdd;
    this.interval = interval;
    this.lineNum = lineNum;
    this.turnFlg = turnFlg;
    this.turnInterval = turnInterval;
    vector = 3;
  }
  
  //**弾幕を描画
  public void draw(int status){
    if(status == Const.STATUS_ENEMY_ACTIVE && frameCount%interval == 0){
      angle += angleAdd;
      if(angle>2*PI){ angle -= 2*PI; }
      for(int i=0;i<lineNum;i++){
        float angleTemp = angle + (2*PI)/lineNum * i;
        if(angleTemp>2*PI){ angleTemp -= 2*PI; }
        switch(bulletType){
          case Const.BULLET_TYPE_SMALL:
            bulletList.add(new SmallBullet(new PVector(enemyLocation.x,enemyLocation.y),new PVector(cos(angleTemp)*vector,sin(angleTemp)*vector),null,color(98,216,198)));
            break;
          case Const.BULLET_TYPE_LARGE:
            bulletList.add(new LargeBullet(new PVector(enemyLocation.x,enemyLocation.y),new PVector(cos(angleTemp)*vector,sin(angleTemp)*vector),null,color(98,216,198)));
            break;
        }
      }
      if(turnFlg && frameCount%turnInterval == 0){
        angleAdd *= -1;
      }
      
    }
    drawBulletHell();
    deleteBullet();
  }
  
}

/*------------------------------------------------------------*/
/* ハート型弾幕 */
class HeartBulletHell extends BulletHell{
  PVector enemyLocation;
  float x;
  float y;
  float xFormula;
  float yFormula;
  int col = color(241,141,158);
  int myFrameCount;
  
  HeartBulletHell(PVector enemyLocation){
    super();
    this.enemyLocation = enemyLocation;
    myFrameCount = frameCount-70;
  }
  
  //**弾幕を描画
  public void draw(int status){
    //弾幕生成
    int interval = PApplet.parseInt(random(90,131));
    if(frameCount - myFrameCount >= interval){  
      myFrameCount = frameCount;
      for (int i = 0; i < 360; i+=5) {
        xFormula = (16 * sin(radians(i)) * sin(radians(i)) * sin(radians(i)));
        yFormula = (13 * cos(radians(i)) - 5 * cos(radians(2 * i)) - 2 * cos(radians(3 * i)) - cos(radians(4 * i))) * (-1);
        x = 5 * xFormula + enemyLocation.x;
        y = 5 * yFormula + enemyLocation.y;
        bulletList.add(new SmallBullet(new PVector(x,y),new PVector(xFormula*0.1f,yFormula*0.1f),null,col));
      }
    }
    drawBulletHell();
    deleteBullet(); 
  }
}

/*------------------------------------------------------------*/
/* バウンド弾幕(スペカ) */
class BoundBulletHell extends BulletHell{
  float angle;
  int colNo;
  int col[] = {color(98,216,198),color(241,141,158),color(235,223,0)};
  
  BoundBulletHell(){
    super();
  }

  //**弾幕を描画
  public void draw(int status){
    //弾幕生成、バウンド処理
    if(frameCount%150 == 0){
      for(int i=0; i<3; i++){
        //初期位置、進行方向はランダム
        angle = random(2*PI);
        colNo = PApplet.parseInt(random(0,3));
        if(colNo == 3){ colNo = 2; }
        bulletList.add(new SmallBullet(new PVector(random(0,width),random(Const.HEIGHT_INFO,400)),new PVector(cos(angle)*2.5f,sin(angle)*2.5f),null,col[colNo]));
        bulletList.add(new LargeBullet(new PVector(random(0,width),random(Const.HEIGHT_INFO,400)),new PVector(cos(angle)*2.5f,sin(angle)*2.5f),null,col[colNo]));
      }
    }
    bound();
    
    drawBulletHell();
    deleteBullet();     
  }
  
  //**バウンド処理
  public void bound(){
    for(Bullet b : bulletList){
      if(b.getBoundNum() >= 2){ continue; }
      float locationX = b.getLocation().x;
      float locationY = b.getLocation().y;
      float velocityX = b.getVelocity().x;
      float velocityY = b.getVelocity().y;
      if(locationX <= (0 + Const.SMALLBULLET_DIAMETER/2) || locationX >= (width - Const.SMALLBULLET_DIAMETER/2)){
        velocityX *= -1;
        b.countBoundNum();
      }
      if(locationY <= (Const.HEIGHT_INFO + Const.SMALLBULLET_DIAMETER/2) || locationY >= (height - Const.SMALLBULLET_DIAMETER/2)){
        velocityY *= -1;
        b.countBoundNum();
      }
      b.setVelocity(new PVector(velocityX,velocityY));
    }
  }
  
  
}

/*------------------------------------------------------------*/
/* レーザータイプ */
/*------------------------------------------------------------*/
/* 放射レーザー弾幕クラス */
class AllRoundLaserBulletHell extends BulletHell{
  PVector enemyLocation;  //敵機位置
  int num;      //レーザー本数
  float laserLen;  //レーザー長さ
  int rotateFlg;  //回転フラグ
  int col = color(98,216,198);
  
  float rotateCnt = 0.0f;  //回転係数
  
  AllRoundLaserBulletHell(PVector enemyLocation,float laserRange,int num,float laserLen,int rotateFlg){
    super();
    this.enemyLocation = enemyLocation;
    this.num = num;
    this.laserLen = laserLen;
    this.rotateFlg = rotateFlg;
    
    //レーザー生成(※途中での生成はないものとする)
    for(int i=0;i<this.num;i++){
      float angle = (2*PI/this.num) * i;
      laserList.add(new NormalLaser(new PVector(this.enemyLocation.x,this.enemyLocation.y)
                                    ,new PVector((cos(angle)*this.laserLen)+this.enemyLocation.x,(sin(angle)*this.laserLen)+this.enemyLocation.y)
                                    ,null
                                    ,laserRange
                                    ,Const.LASER_PRE_TIME_NORMAL,col));
    }
    
  }

  //**弾幕を描画
  public void draw(int status){
    
    float angle = 0.0f;  //回転角度
    float addAngle = 0.0f;//回転角度増分
    boolean angleUpdateFlg = false;  //角度更新フラグ
    
    //*回転なし
    if(rotateFlg == Const.LASER_ROTATE_OFF){
      drawBulletHell();
      return;
    }
    
    //*回転あり
    for(int i=0;i<num;i++){
      Laser l = laserList.get(i);
      if(l.getStatus() == Const.LASER_STATUS_SHOOT){
        angleUpdateFlg = true;
        //角度を回転させる
        addAngle = (2*PI/360) * rotateCnt;
        if(rotateFlg == Const.LASER_ROTATE_CLOCKWISE){
          angle = (2*PI/num) * i + addAngle;
        }else if(rotateFlg == Const.LASER_ROTATE_COUNTER_CLOCKWISE){
          angle = (2*PI/num) * i - addAngle;
        }
        //レーザーの終点位置を更新
        l.setEndPoint(new PVector((cos(angle)*this.laserLen)+this.enemyLocation.x,(sin(angle)*this.laserLen)+this.enemyLocation.y));
      }
    }
    
    drawBulletHell();
    if(angleUpdateFlg){
      //回転分の角度を更新(1フレームで0.1°回転)
      rotateCnt = ( rotateCnt >= 360 ? 0 : rotateCnt + 0.1f);
    }
    
  }
    
}

/*------------------------------------------------------------*/
/* 全体レーザー弾幕クラス */
class WideLaserBulletHell extends BulletHell{
  
  int num;      //レーザー本数
  float laserRange;  //レーザー幅
  int myFrameCount;  //インスタンス生成時のフレームカウント
  int col = color(72,151,216);
  
  WideLaserBulletHell(float laserRange,int num){
    super();
    this.num = num;
    this.laserRange = laserRange;
    this.myFrameCount = frameCount;
  }
  
  //**弾幕を描画
  public void draw(int status){
    //レーザーを消す
    if((frameCount-myFrameCount)%600 == 0){
      laserList.clear();
      return;
    }
    //レーザーを一定時間毎に作成
    if((frameCount-myFrameCount)%300 == 0){
      //myFrameCount = frameCount;
      for(int i=0; i<num; i++){
        float p = height/num * i + Const.HEIGHT_INFO;
        if(i%2 != 0){
          laserList.add(new NormalLaser(new PVector(0,p),new PVector(width,random(p-150,p+150)),null,laserRange,Const.LASER_PRE_TIME_LONG,col));
        }else{
          laserList.add(new NormalLaser(new PVector(0,random(p-150,p+150)),new PVector(width,p),null,laserRange,Const.LASER_PRE_TIME_LONG,col));
        }
      }
    }
    drawBulletHell();
  }
  
}

/*------------------------------------------------------------*/
/* 花型レーザー弾幕クラス（スペカ） */
class FlowerLikeLaserBulletHell extends BulletHell{

  ArrayList<PVector> locationList;  //位置情報
  int num;          //花数
  float laserLen;  //レーザー長
  int drawCount = 0;  //描画回数
  int blackout = 0;  //暗転
  int myFrameCount = 0;
  int col = color(98,216,198);
  
  FlowerLikeLaserBulletHell(int num,float laserLen){
    super();
    this.num = num;
    this.laserLen = laserLen;
    locationList = new ArrayList<PVector>();
    createLocation();
  }
  
  //**位置生成
  public void createLocation(){
    for(int i=0; i<num; i++){
      locationList.add(new PVector(random(50,550),random(150,400)));
    }
    num++;
  }
  
  
  //**弾幕を描画
  public void draw(int status){
    
    if(drawCount == 0){ myFrameCount = frameCount; }
    
    //レーザー生成
    if(frameCount%5==0 && drawCount < 12){
      float angle = 2*PI/12 * drawCount;
      for(PVector loc : locationList){
        laserList.add(new NormalLaser(
                        new PVector(loc.x,loc.y)
                        ,new PVector(cos(angle)*laserLen + loc.x,sin(angle)*laserLen + loc.y)
                        ,new PVector(cos(angle)*3.5f,sin(angle)*3.5f)
                        ,3,Const.LASER_PRE_TIME_INVALID,col));
      }
      drawCount++;
    }
    
    //暗転
    if(drawCount >= 12 && blackout < 45){
      //println("暗転開始");
      bgCol = color(Const.BACKGROUND_COLOR_BLACKOUT);
      blackout++;
    }else if(blackout == 45){
      //println("暗転終了");
      bgCol = color(Const.BACKGROUND_COLOR_NORMAL);
      blackout++;
    }
    
    //レーザー有効化
    if(drawCount >= 12 && blackout == 46){ 
      for(Laser l : laserList){ 
        l.setStatus(Const.LASER_STATUS_SHOOT);
        l.setMoveStatus(Const.LASER_MOVE_STATUS_MOVE);
      }
      blackout++;
      myFrameCount = frameCount;
    }
    
    drawBulletHell();
    
    //弾幕リフレッシュ
    if((frameCount-myFrameCount)>=240){
      //println("弾幕リフレッシュ");
      laserList.clear();
      locationList.clear();
      drawCount = 0;
      blackout = 0;
      createLocation();
    }
    
  } 
  
  
}
/* チャプタークラス(基底) */
abstract class Chapter{
  
  protected int chapterNo;  //チャプター番号
  protected ArrayList<Enemy> enemyList;  //敵リスト
    
  //**コンストラクタ
  Chapter(){
    
    chapterNo = 0;
    enemyList = new ArrayList<Enemy>();
    
  }
  
  //**敵を生成
  public abstract void createEnemy(Player player);
  
  //**チャプターシナリオを実行
  public abstract void exec(Player player);

  
  //**チャプターが終了したか判定
  public boolean isChapterEnd(){
    for(Enemy e : enemyList){
      //処理中の敵がある場合、未終了
      if(e.getStatus() != Const.STATUS_ENEMY_DONE){ return false; }
    }
    return true;
  }
  
  //**アクティブ状態の敵が存在するか判定
  public boolean isExistActiveEnemy(){
    for(Enemy e : enemyList){
      if(e.getStatus() == Const.STATUS_ENEMY_ACTIVE){ return true; }
    }
    return false;
  }
  
  //**基本シナリオ
  public void execBaseSenario(Enemy e,Player player){
    /*敵機、弾幕の描画*/
    e.updateLocation();
    e.draw();
    e.drawBulletHell();
    
    /*敵機への攻撃・撃破判定*/
    e.judgeHitToEnemy(player);
    if(e.isDefeat()){
      //println("敵機撃破");
      music.playDefeteEnemy();
      Score.addDefeatBonus(e);
      e.setStatus(Const.STATUS_ENEMY_NOT_ACTIVE);
      if(!e.getBulletRemainFlg()){
        e.deleteAllBullet();
      }
      if(e.bossFlg && e.getMissCount()==0){
        Score.addSpellClearBonus();
      }
    }

    /*敵機の画面アウト判定*/
    if(e.isOutOfScreen()){
      e.setStatus(Const.STATUS_ENEMY_NOT_ACTIVE);
      if(!e.getBulletRemainFlg()){
        e.deleteAllBullet();
      }
    }
    
    /*敵機のタイムアウト判定*/
    if(e.isTimeOut()){
      //println("敵機タイムアウト");
      e.setStatus(Const.STATUS_ENEMY_NOT_ACTIVE);
      if(!e.getBulletRemainFlg()){
        e.deleteAllBullet();
      }
      if(e.bossFlg && e.getMissCount()==0){
        Score.addSpellClearBonus();
      }
    }
    
    /*敵機の処理終了判定*/
    if(e.isDone()){
      e.setStatus(Const.STATUS_ENEMY_DONE);
    }
    
    /*自機のグレイズ判定*/
    if(player.getStatus() != Const.STATUS_PLAYER_MUTEKI){
      for(int i=0; i<e.calcGrazeNum(player);i++){
        Score.addGrazeBonus();
        music.playGraze();
      }
    }

    /*自機への弾幕当たり判定*/
    if(player.getStatus() != Const.STATUS_PLAYER_MUTEKI && e.isHitBulletToPlayer(player)){
      player.hit();
      e.addMissCount();
    }
    
    /*自機の敵機への衝突判定*/
    if(e.getStatus() == Const.STATUS_ENEMY_ACTIVE && e.isHitEnemyToPlayer(player)){
      player.hitEnemy(e);
      e.addMissCount();
    }
    
  }
  
  //**シナリオ:前の敵機が非アクティブになったら次の敵機が出てくる
  public void execNormalSenario(Player player){
    
    int beforeStatus = 99;
    for(int i=0; i<enemyList.size(); i++){
      
      Enemy e = enemyList.get(i);  
      
      //最初の敵をアクティブ状態に
      if(i==0 && e.getStatus() == Const.STATUS_ENEMY_WAIT){
        e.setStatus(Const.STATUS_ENEMY_ACTIVE);
      }
      //前の敵が非アクティブor終了したら、次の敵をアクティブ状態にする
      if(e.getStatus() == Const.STATUS_ENEMY_WAIT){
        if(beforeStatus == Const.STATUS_ENEMY_NOT_ACTIVE || beforeStatus == Const.STATUS_ENEMY_DONE){
          e.setStatus(Const.STATUS_ENEMY_ACTIVE);
        }
      }
      //待機中および処理終了の敵はスキップ
      if(e.getStatus() == Const.STATUS_ENEMY_WAIT || e.getStatus() == Const.STATUS_ENEMY_DONE){
        continue;
      }
      
      //実行
      execBaseSenario(e,player);
      beforeStatus = e.getStatus();
      
    }    
   
    /*自機の時間経過に伴う状態制御*/
    player.updateStatusByTime();
            
    /*ノーミスボーナス*/
    if(player.getNoMissTime()>= Const.NO_MISS_TIME && isExistActiveEnemy()){
      Score.addNoMissBonus();
      //println("一定時間ノーミスボーナス : " + Score.getScore() + " frameCount : " + frameCount);
      player.initNoMissTime();
    }
    
    /*エクステンド判定*/
    if(Score.getGrazeExtend() >= Const.EXTEND_POINT_GRAZE){
      player.extend();
      Score.initGrazeEntend();
    }

  }
  
  //**シナリオ:敵機が一気に出撃
  public void execAtOnceSenario(Player player){
    for(Enemy e : enemyList){
      //最初はアクティブ化
      if(e.getStatus() == Const.STATUS_ENEMY_WAIT){
        e.setStatus(Const.STATUS_ENEMY_ACTIVE);
      }
      //待機中および処理終了の敵はスキップ
      if(e.getStatus() == Const.STATUS_ENEMY_WAIT || e.getStatus() == Const.STATUS_ENEMY_DONE){
        continue;
      }
      
      //実行
      execBaseSenario(e,player);
    }

    /*自機の時間経過に伴う状態制御*/
    player.updateStatusByTime();
    
    /*ノーミスボーナス*/
    if(player.getNoMissTime()>= Const.NO_MISS_TIME && isExistActiveEnemy()){
      Score.addNoMissBonus();
      //println("一定時間ノーミスボーナス : " + Score.getScore() + " frameCount : " + frameCount);
      player.initNoMissTime();
    }

    /*エクステンド判定*/
    if(Score.getGrazeExtend() >= Const.EXTEND_POINT_GRAZE){
      player.extend();
      Score.initGrazeEntend();
    }

  }
  
  //**シナリオ:一定間隔で敵機が出てくる
  public void execConstantSenario(Player player,int interval){
    for(int i=0; i<enemyList.size();i++){
      
      Enemy beforeE = null;
      if(i != 0){ beforeE = enemyList.get(i-1); }
      Enemy currentE = enemyList.get(i);      
      
      //最初の敵をアクティブ状態に
      if(i==0 && currentE.getStatus() == Const.STATUS_ENEMY_WAIT){
        currentE.setStatus(Const.STATUS_ENEMY_ACTIVE);
      }
      //一定時間経過したら次の敵をアクティブ状態に
      if(currentE.getStatus() == Const.STATUS_ENEMY_WAIT){
        if(beforeE != null && beforeE.getActiveTime() >= interval){
          currentE.setStatus(Const.STATUS_ENEMY_ACTIVE);
        }
      }
      //待機中および処理終了の敵はスキップ
      if(currentE.getStatus() == Const.STATUS_ENEMY_WAIT || currentE.getStatus() == Const.STATUS_ENEMY_DONE){
        continue;
      }
      
      //実行
      execBaseSenario(currentE,player);
    }

    /*自機の時間経過に伴う状態制御*/
    player.updateStatusByTime(); 

    /*ノーミスボーナス*/
    if(player.getNoMissTime()>= Const.NO_MISS_TIME && isExistActiveEnemy()){
      Score.addNoMissBonus();
      //println("一定時間ノーミスボーナス : " + Score.getScore() + " frameCount : " + frameCount);
      player.initNoMissTime();
    }

    /*エクステンド判定*/
    if(Score.getGrazeExtend() >= Const.EXTEND_POINT_GRAZE){
      player.extend();
      Score.initGrazeEntend();
    }

}
  
}
/*------------------------------------------------------------*/
/*チャプター1*/
class Chapter1 extends Chapter{
  
  Chapter1(){
    super();
    chapterNo = 1;
  }

  //**敵を生成
  public void createEnemy(Player player){
    //全方位弾敵*3
    enemyList.add(new Enemy001(60.0f,new PVector(width/2,0.0f),new PVector(0.0f,2.0f),240,Const.BULLET_TYPE_SMALL,true,false));
    enemyList.add(new Enemy001(60.0f,new PVector(width/4,0.0f),new PVector(0.0f,2.0f),240,Const.BULLET_TYPE_SMALL,true,false));
    enemyList.add(new Enemy001(60.0f,new PVector(3*width/4,0.0f),new PVector(0.0f,2.0f),240,Const.BULLET_TYPE_SMALL,true,false));
  }
  
  //**チャプターシナリオを実行
  public void exec(Player player){

    /*★★シナリオ概要：全方位弾*3 撃破したら次の敵機が出てくる★★*/
    execNormalSenario(player);
    
  }

}

/*------------------------------------------------------------*/
/*チャプター2*/
class Chapter2 extends Chapter{
  
  Chapter2(){
    super();
    chapterNo = 2;
  }

  //**敵を生成
  public void createEnemy(Player player){
    //自機狙い弾敵*3
    enemyList.add(new Enemy002(Const.HP_ENEMY_INVALID,new PVector(0.0f,100.0f),new PVector(3.0f,0.0f),player.getLocation(),Const.TIMEOUT_ENEMY_INVALID,true,false));
    enemyList.add(new Enemy002(Const.HP_ENEMY_INVALID,new PVector(width,100.0f),new PVector(-3.0f,0.0f),player.getLocation(),Const.TIMEOUT_ENEMY_INVALID,true,false));
    enemyList.add(new Enemy002(Const.HP_ENEMY_INVALID,new PVector(0.0f,100.0f),new PVector(3.0f,0.0f),player.getLocation(),Const.TIMEOUT_ENEMY_INVALID,true,false));
    //ランダム弾*3
    enemyList.add(new Enemy003(60.0f,new PVector(width/2,0.0f),new PVector(0.0f,2.0f),240,Const.BULLET_TYPE_LARGE,true,false,6));
    enemyList.add(new Enemy003(60.0f,new PVector(width/4,0.0f),new PVector(0.0f,2.0f),240,Const.BULLET_TYPE_LARGE,true,false,6));
    enemyList.add(new Enemy003(60.0f,new PVector(3*width/4,0.0f),new PVector(0.0f,2.0f),240,Const.BULLET_TYPE_LARGE,true,false,6));
  }

  //**チャプターシナリオを実行
  public void exec(Player player){

    /*★★シナリオ概要：自機狙い弾敵*3+ランダム弾*3 画面アウトしたら次の敵が出てくる★★*/
    execNormalSenario(player);
  }
  
}

/*------------------------------------------------------------*/
/*チャプター3*/
class Chapter3 extends Chapter{

  Chapter3(){
    super();
    chapterNo = 3;
  }

  //**敵を生成
  public void createEnemy(Player player){
    //放射レーザー*2
    enemyList.add(new Enemy004(
                        Const.HP_ENEMY_INVALID
                        ,new PVector(width/8,100.0f)
                        ,new PVector(0.0f,0.0f)
                        ,16
                        ,Const.LASER_ROTATE_COUNTER_CLOCKWISE
                        ,180
                        ,false
                        ,false));
    enemyList.add(new Enemy004(
                        Const.HP_ENEMY_INVALID
                        ,new PVector(7*width/8,100.0f)
                        ,new PVector(0.0f,0.0f)
                        ,16
                        ,Const.LASER_ROTATE_CLOCKWISE
                        ,180
                        ,false
                        ,false));
  }
  
  //**チャプターシナリオを実行+
  public void exec(Player player){
    execAtOnceSenario(player);
  } 
  
}

/*------------------------------------------------------------*/
/*チャプター4*/
class Chapter4 extends Chapter{
  
  Chapter4(){
    super();
    chapterNo = 4;
  }

  //**敵を生成
  public void createEnemy(Player player){
    //放射レーザー
    enemyList.add(new Enemy004(
                        Const.HP_ENEMY_INVALID
                        ,new PVector(width/2,100.0f)
                        ,new PVector(0.0f,0.0f)
                        ,9
                        ,Const.LASER_ROTATE_OFF
                        ,600
                        ,false
                        ,false));
    //ランダム弾*2                    
    enemyList.add(new Enemy003(180.0f,new PVector(width/4,0.0f),new PVector(0.0f,2.0f),600,Const.BULLET_TYPE_SMALL,true,false,3));
    enemyList.add(new Enemy003(180.0f,new PVector(3*width/4,0.0f),new PVector(0.0f,2.0f),600,Const.BULLET_TYPE_SMALL,true,false,3));
  }
  
  //**チャプターシナリオを実行
  public void exec(Player player){
    execAtOnceSenario(player);
  }  
  
}

/*------------------------------------------------------------*/
/*チャプター5*/
class Chapter5 extends Chapter{

  Chapter5(){
    super();
    chapterNo = 5;
  }
  
  //**敵を生成
  public void createEnemy(Player player){
    for(int i=0; i<20; i++){
      //レーザー弾幕
      enemyList.add(new Enemy005(Const.HP_ENEMY_INVALID,new PVector(random(width),random(Const.HEIGHT_INFO,250)),new PVector(0,1),60,PI/2,true,false));
    }
  }
  
  //**チャプターシナリオを実行
  public void exec(Player player){
    execConstantSenario(player,30);
  }
  
}

/*------------------------------------------------------------*/
/*チャプター6*/
class Chapter6 extends Chapter{

  Chapter6(){
    super();
    chapterNo = 6;
  }
  
  //**敵を生成
  public void createEnemy(Player player){
    enemyList.add(new Enemy006(90,new PVector(width/2,0.0f),new PVector(0,2),480,Const.BULLET_TYPE_SMALL,true,false,radians(22)));
    enemyList.add(new Enemy006(90,new PVector(width/4,0.0f),new PVector(0,2),480,Const.BULLET_TYPE_SMALL,true,false,radians(22)));
    enemyList.add(new Enemy006(90,new PVector(3*width/4,0.0f),new PVector(0,2),480,Const.BULLET_TYPE_SMALL,true,false,radians(22)));
    //複数らせん(角度増分3°、発射間隔1、放射数5、転換あり、転換間隔20
    //enemyList.add(new Enemy007(90,new PVector(width/2,100.0),new PVector(0,2),Const.TIMEOUT_ENEMY_INVALID,Const.BULLET_TYPE_SMALL,true,false,radians(3),1,4,true,20));
  }
  
  //**チャプターシナリオを実行
  public void exec(Player player){
    execNormalSenario(player);
  }
  
}

/*------------------------------------------------------------*/
/*チャプター7*/
class Chapter7 extends Chapter{

  Chapter7(){
    super();
    chapterNo = 7;
  }
  
  //**敵を生成
  public void createEnemy(Player player){
    //複数らせん(角度増分2°、発射間隔6、放射数5、転換なし）
    enemyList.add(new Enemy007(90,new PVector(width/2,100.0f),new PVector(0,2),360,Const.BULLET_TYPE_SMALL,true,false,radians(2),12,5,false,1));
    //全方位弾
    enemyList.add(new Enemy001(60.0f,new PVector(width/5,0.0f),new PVector(0.0f,2.0f),240,Const.BULLET_TYPE_SMALL,true,false));
    enemyList.add(new Enemy001(60.0f,new PVector(4*width/5,0.0f),new PVector(0.0f,2.0f),240,Const.BULLET_TYPE_SMALL,true,false));
  }

  //**チャプターシナリオを実行
  public void exec(Player player){
    execAtOnceSenario(player);
  }
  
}
/*------------------------------------------------------------*/
/*ボス戦*/
class BossChapter extends Chapter{

  BossChapter(){
    super();
    chapterNo = 5;
  }

  //**敵を生成
  public void createEnemy(Player player){
    enemyList.add(new Boss001(1200,new PVector(width/2,100),new PVector(0,0),3600,false,true));
    enemyList.add(new Boss002(300,new PVector(width/2,250),new PVector(0,0),1800,false,true));
    enemyList.add(new Boss003(600,new PVector(width/2,100),new PVector(0,0),3600,false,true));
    enemyList.add(new Boss004(1200,new PVector(width/2,100),new PVector(0,0),3600,false,true));
  }
  
  //**チャプターシナリオを実行+
  public void exec(Player player){
    execNormalSenario(player);
  }  
  
}
/* 定数定義 */

class Const{
  //--------------------------------------------------------------------------------------
  //System
  //--------------------------------------------------------------------------------------
  //モード
  final static int MODE_DEV = 0;  //(ゲームオーバーにならない)
  final static int MODE_PRO = 1;
  
  //シーン番号
  final static int SCENE_NO_START = 99;
  final static int SCENE_NO_TITLE = 0;
  final static int SCENE_NO_OPENING = 1;
  final static int SCENE_NO_STAGE1 = 2;
  final static int SCENE_NO_PRE_BOSS = 3;
  final static int SCENE_NO_BOSS = 4;
  final static int SCENE_NO_CLEAR = 5;
  final static int SCENE_NO_GAMEOVER = 6;

  //画面サイズ(staticクラス内など、直接width,heightプロパティを読めない場所があれば使用)
  final static int WIDTH_SCREEN = 600;
  final static int HEIGHT_SCREEN = 750;
  
  //上部情報画面
  final static float HEIGHT_INFO = 50.0f;

  //キー操作フラグ
  final static int KEY_FLG_PRESS = 1;
  final static int KEY_FLG_RELEASE = 0;
  
  //キー種類
  final static char KEY_SHOOT = 'z';   //ショット発射(z)
 
  //移動方向
  final static int DIRECTION_UP = 1;
  final static int DIRECTION_DOWN = 2;
  final static int DIRECTION_LEFT = 3;
  final static int DIRECTION_RIGHT = 4;
    
  //チャプター数
  final static int CHAPTER_NUM = 5; //不使用かも?
  
  //自機ステータス
  final static int STATUS_PLAYER_NON = 0;      //ノーショット状態
  final static int STATUS_PLAYER_SHOOT = 1;    //ショット状態
  final static int STATUS_PLAYER_BOM = 2;      //ボム状態(未実装)
  final static int STATUS_PLAYER_MUTEKI = 3;   //被弾後無敵時間

  //自機当たり判定の半径
  final static float RANGE_HIT_PLAYER = 5.0f;
  
  //自機被弾後の無敵時間(フレーム数)
  final static int MUTEKI_TIME_PLAYER = 60;

  //残機
  final static int ZANKI_MAX = 7;
  final static int EXTEND_POINT_GRAZE = 1000;
  
  //敵機ステータス
  final static int STATUS_ENEMY_WAIT = 0;          //待機状態
  final static int STATUS_ENEMY_ACTIVE = 1;        //アクティブ状態(プレイヤーから見えている状態の敵)
  final static int STATUS_ENEMY_NOT_ACTIVE = 2;    //非アクティブ状態(撃破済み、画面アウトなどで　プレイヤーから見えてない状態の敵)　
                                                   //※敵機が消えても弾幕は生きていることがある
                                                   //※元から非描画の敵を非アクティブ状態に含めるかは要検討
  final static int STATUS_ENEMY_DONE = 3;          //終了状態
  
  //敵機体力
  final static int HP_ENEMY_INVALID = -999;  //敵機体力(撃破不能な敵)
  
  //敵機タイムアウト
  final static int TIMEOUT_ENEMY_INVALID = 0;  //敵機タイムアウト時間(無効) 
    
  //弾種
  final static String BULLET_TYPE_SMALL = "SmallBullet";    //小弾
  final static String BULLET_TYPE_LARGE = "LargeBullet";    //大弾
  
  //弾仕様
  final static float SMALLBULLET_DIAMETER = 10.0f;  //小弾直径
  final static float SMALLBULLET_RANGE = 3.0f;  //小弾当たり判定半径
  final static float SMALLBULLET_GRAZE = 15.0f;  //小弾グレイズ判定半径
  final static float LARGEBULLET_DIAMETER = 30.0f;  //大弾直径
  final static float LARGEBULLET_RANGE = 10.0f;  //大弾当たり判定半径
  final static float LARGEBULLET_GRAZE = 25.0f;  //大弾グレイズ判定半径
  
  //レーザー弾回転有無
  final static int LASER_ROTATE_OFF = 0;
  final static int LASER_ROTATE_CLOCKWISE = 1;          //時計回り
  final static int LASER_ROTATE_COUNTER_CLOCKWISE = 2;  //反時計回り
  
  //レーザーステータス
  final static int LASER_STATUS_PRE = 1;    //予告状態
  final static int LASER_STATUS_SHOOT = 2;  //実ショット状態
  
  //レーザー動作ステータス
  final static int LASER_MOVE_STATUS_STOP = 1;    //静止状態
  final static int LASER_MOVE_STATUS_MOVE = 2;    //動きあり  
 
  //レーザー予告時間
  final static int LASER_PRE_TIME_NORMAL = 60;  //通常(60フレーム)
  final static int LASER_PRE_TIME_LONG = 120;  //長い(120フレーム)
  final static int LASER_PRE_TIME_INVALID = -999;  //無効(=別途フラグ管理にて予告時間制御)

  //スコア
  final static int SCORE_BOSS_DEFEAT = 10141;  //ボス撃破
  final static int SCORE_BOSS_SPELLCLEAR = 5039;  //ボススペルクリア(ノー撃破)
  final static int SCORE_ENEMY_DEFEAT = 293;  //通常敵撃破
  final static int SCORE_GRAZE = 3;  //グレイズ
  final static int SCORE_NO_MISS = 2;  //一定時間ノー被弾
  final static int SCORE_CLEAR = 1000;  //クリアボーナス(残機1つあたり)
  
  final static int NO_MISS_TIME = 5;  //一定時間ノー被弾ボーナス付与タイム(フレーム数)

  //--------------------------------------------------------------------------------------
  //graphic、演出
  //--------------------------------------------------------------------------------------
  
  //自機グラフィック
  final static float PLAYER_GRAPHIC_WIDTH = 35.0f;
  final static float PLAYER_GRAPHIC_HEIGHT = 53.0f;
  
  //ボスグラフィック
  final static float BOSS_GRAPHIC_WIDTH = 50.0f;
  final static float BOSS_GRAPHIC_HEIGHT = 82.0f;
  final static float BOSS_HPGAUGE_DIAMETER = 90.0f;

  //UFOグラフィック
  final static float UFO_GRAPHIC_WIDTH = 60.0f;
  final static float UFO_GRAPHIC_HEIGHT = 56.0f;
  final static float UFO_HPGAUGE_DIAMETER = 90.0f;

  //自機カットグラフィック
  final static float PLAYERCUT_GRAPHIC_WIDTH = 100.0f;
  final static float PLAYERCUT_GRAPHIC_HEIGHT = 150.0f;

  //ボスカットグラフィック
  final static float BOSSCUT_GRAPHIC_WIDTH = 100.0f;
  final static float BOSSCUT_GRAPHIC_HEIGHT = 163.0f;
 
 //会話
 final static int CAPTION_NUM_PRE_BOSS = 27;
 
  //背景色（グレイスケール）
  final static int BACKGROUND_COLOR_NORMAL = 0;
  final static int BACKGROUND_COLOR_BLACKOUT = 25;
}
/*敵機クラス(基底)*/
abstract class Enemy{
  
  protected float hp;  //体力
  protected float maxHp;  //最大体力
  protected int status;  //ステータス
  protected float range;  //敵機当たり判定の半径
  protected PVector location;  //敵機の位置ベクトル
  protected PVector direction;  //敵機の移動方向ベクトル
  protected int activeTime = 0;  //アクティブ時間(フレーム数)
  protected int timeout;    //タイムアウト時間(フレーム数)
  protected boolean bulletRemainFlg;  //弾幕残しフラグ(敵機非アクティブ化後、弾幕を即消去するか残すか)
  protected boolean bossFlg;  //ボスフラグ
  protected int missCount = 0;    //自機被弾回数
  protected ArrayList<BulletHell> bulletHellList;  //弾幕オブジェクトリスト
  
  Enemy(float hp,PVector location,PVector direction,int timeout,boolean bulletRemainFlg,boolean bossFlg){
    
    //個別セット
    this.status = Const.STATUS_ENEMY_WAIT;
    this.hp = hp;
    this.maxHp = hp;
    this.location = location;
    this.direction = direction;
    this.timeout = timeout;
    this.bulletRemainFlg = bulletRemainFlg;
    this.bossFlg = bossFlg;
    
    //デフォルト値セット(サブクラスで実値セット)
    range = 0.0f;
    bulletHellList = new ArrayList<BulletHell>();
    
  }
  
  //**敵機の位置を更新
  public abstract void updateLocation();
  
  //**敵機を描画
  public abstract void draw();
  
  //**ボスを描画
  public void drawBoss(){
     //ボスグラフィック
    img.drawBossImage(location);
    //体力ゲージ
    noFill();
    stroke(176,196,222);
    strokeWeight(3);
    pushMatrix();
    translate(location.x,location.y);
    rotate(3*PI/2);
    float angle = (360/maxHp) * (maxHp-hp);
    arc(0,0,Const.BOSS_HPGAUGE_DIAMETER,Const.BOSS_HPGAUGE_DIAMETER,radians(angle),radians(360));
    popMatrix();
  }

  //**敵機から射出する弾幕を描画
  public void drawBulletHell(){
    for(BulletHell bulletHell : bulletHellList){
      bulletHell.draw(status);
    }
  }
  
  //**敵機の体力を計算(メモ：減少仕方を変更したい場合、各クラスでオーバーライドする)
  public void calcHP(){
    hp = hp - 1;
  }

  //**敵機への自機ショット当たり判定
  public void judgeHitToEnemy(Player player){
    //撃破不能の敵:判定なし
    if(hp == Const.HP_ENEMY_INVALID){ return; }
    //敵機が非アクティブ:判定なし
    if(status == Const.STATUS_ENEMY_NOT_ACTIVE){ return; }
    
    //当たり判定実施
    if(player.getStatus() == Const.STATUS_PLAYER_SHOOT){
      if(player.getLocation().x > location.x - range && player.getLocation().x < location.x + range && player.getLocation().y > location.y){
        calcHP();
      }
    }
  }
  
  //**敵機撃破判定
  public boolean isDefeat(){
    //アクティブ状態かつ、hpが0以下、かつ撃破不能的ではない
    return ( status == Const.STATUS_ENEMY_ACTIVE && hp <= 0  && hp != Const.HP_ENEMY_INVALID ? true : false); 
  }
  
  //**自機への弾幕当たり判定
  public boolean isHitBulletToPlayer(Player player){
    boolean isHit = false;
    for(BulletHell bulletHell : bulletHellList){
      if(bulletHell.isHitToPlayer(player)){
        isHit = true;
      }
    }
    return isHit;
  }
  
  //**自機へのグレイズ判定
  public int calcGrazeNum(Player player){
    int grazeNum = 0;
    for(BulletHell bulletHell : bulletHellList){
      grazeNum += bulletHell.calcGrazeNum(player);
    }
    return grazeNum;
  }
  
  //**自機への敵機本体当たり判定
  public boolean isHitEnemyToPlayer(Player player){
     //自機位置が敵機当たり範囲内
    if(player.getLocation().x > location.x - range && player.getLocation().x < location.x + range){
      if(player.getLocation().y > location.y - range && player.getLocation().y < location.y + range){
        return true;
      }
    }   
    return false;
  }

  //**敵機の画面外判定
  public boolean isOutOfScreen(){
    //アクティブ状態でない:判定なし
    if(status != Const.STATUS_ENEMY_ACTIVE){
      return false;
    }
    
    if(location.x < 0 || location.x > width){
      return true;
    }
    if(location.y < 0 || location.y > height){
      return true;
    }
    return false;
    
  }
  
  //**敵機のタイムアウト判定
  public boolean isTimeOut(){
    //アクティブ状態でない:判定なし
    if(status != Const.STATUS_ENEMY_ACTIVE){
      return false;
    }
    
    if(timeout != Const.TIMEOUT_ENEMY_INVALID && activeTime >= timeout){
      return true;
    }
    return false;
    
  }
  
  //**弾幕を初期化
  public void deleteAllBullet(){
    for(BulletHell bulletHell : bulletHellList){
      bulletHell.deleteAllBullet();
    }
  }
  
  //**敵機の処理終了判定
  public boolean isDone(){
    
    //まだ敵機が非アクティブになっていない=false
    if(status != Const.STATUS_ENEMY_NOT_ACTIVE){
      return false;
    }
    
    //全ての弾幕の処理が終了していない=false
    for(BulletHell bulletHell : bulletHellList){
      if(bulletHell.getBulletList().size() != 0 || bulletHell.getLaserList().size() != 0){
        return false;
      }
    }
    
    return true;
  }
  
  //**被弾回数カウントアップ
  public void addMissCount(){
    missCount += 1;
    //println("addMissCount : " + missCount);
  }
  
  //**敵機drawパターン1
  public void draw1(){
      //println("draw1");
      noStroke();
      //fill(255,247,153,30);
      //ellipse(location.x,location.y,40,40);
      //fill(255,247,153,50);
      //ellipse(location.x,location.y,30,30);
      //fill(255,247,153,80);
      //ellipse(location.x,location.y,20,20);
      
      fill(255,255,255,30);
      drawStar(location.x,location.y,22,6);
      fill(255,255,255,50);
      //drawStar(location.x,location.y,20,6);
      //fill(255,255,255,70);
      drawStar(location.x,location.y,19,6);
      fill(255,255,255,80);
      drawStar(location.x,location.y,14,6);
      fill(255,255,255,90);
      drawStar(location.x,location.y,10,6);
      
      
      
  }
  
  /*getter,setter*/
  public int getStatus(){
    return status;
  }
  public void setStatus(int status){
    this.status = status;
  }
  public int getActiveTime(){
    return activeTime;
  }
  public boolean getBulletRemainFlg(){
    return bulletRemainFlg;
  }
  public boolean getBossFlg(){
    return bossFlg;
  }
  public int getMissCount(){
    return missCount;
    
  }
}

/*------------------------------------------------------------*/
/*敵機タイプ①　丸型敵機:全方位弾射出*/
class Enemy001 extends Enemy{

  Enemy001(float hp,PVector location,PVector direction,int timeout,String bulletType,boolean bulletRemainFlg,boolean bossFlg){
    super(hp,location,direction,timeout,bulletRemainFlg,bossFlg);
    range = 10.0f;
    bulletHellList.add(new AllRoundBullletHell(20,bulletType,this.location));
  }
  
  //**敵機の位置を更新
  public void updateLocation(){
    if(status == Const.STATUS_ENEMY_ACTIVE){
      if(location.y < 100){
        location.add(direction);    
      }
    }
  }

  //**敵機を描画
  public void draw(){
    if(status == Const.STATUS_ENEMY_ACTIVE){
      draw1();
      activeTime += 1;
    }
  }
  
}

/*------------------------------------------------------------*/
/*敵機タイプ②　丸型敵機:自機狙い弾射出　*/
class Enemy002 extends Enemy{

  Enemy002(float hp,PVector location,PVector direction,PVector playerLocation,int timeout,boolean bulletRemainFlg,boolean bossFlg){
    super(hp,location,direction,timeout,bulletRemainFlg,bossFlg);
    range = 10.0f;
    //自機狙い弾幕生成(敵機位置、自機位置)
    bulletHellList.add(new TargetingBulletHell(this.location,playerLocation));
  }
 
  //**敵機の位置を更新
  public void updateLocation(){
    if(status == Const.STATUS_ENEMY_ACTIVE){
      location.add(direction);
    }
  }
  
  //**敵機を描画
  public void draw(){
    if(status == Const.STATUS_ENEMY_ACTIVE){
      draw1();
      activeTime += 1;
    }
  }
  
}

/*------------------------------------------------------------*/
/*敵機タイプ③　丸型敵機:ランダム水滴弾射出　*/
class Enemy003 extends Enemy{

  Enemy003(float hp,PVector location,PVector direction,int timeout,String bulletType,boolean bulletRemainFlg,boolean bossFlg,int interval){
    super(hp,location,direction,timeout,bulletRemainFlg,bossFlg);
    range = 10.0f;
    //ランダム水滴弾幕生成(敵機位置)
    bulletHellList.add(new RandomBulletHell(this.location,bulletType,interval));
  }

  //**敵機の位置を更新
  public void updateLocation(){
    if(status == Const.STATUS_ENEMY_ACTIVE){
      if(location.y < 100){
        location.add(direction);    
      }
    }
  }
  
  //**敵機を描画
  public void draw(){
    if(status == Const.STATUS_ENEMY_ACTIVE){
      draw1();
      activeTime += 1;
    }
  }
   
}

/*------------------------------------------------------------*/
/*敵機タイプ④　放射レーザー(細)　*/
class Enemy004 extends Enemy{

  Enemy004(float hp,PVector location,PVector direction,int laserNum ,int rotateFlg,int timeout,boolean bulletRemainFlg,boolean bossFlg){
    super(hp,location,direction,timeout,bulletRemainFlg,bossFlg);
    range = 10.0f;
    bulletHellList.add(new AllRoundLaserBulletHell(this.location,3,laserNum,750,rotateFlg));
  }
  
  //**敵機の位置を更新
  public void updateLocation(){
    location.add(direction);
  }
  
  //**敵機を描画
  public void draw(){
    if(status == Const.STATUS_ENEMY_ACTIVE){
      draw1();
      activeTime += 1;
    }
  }
  
}

/*------------------------------------------------------------*/
/*敵機タイプ⑤　レーザー弾幕　*/
class Enemy005 extends Enemy{
  
  Enemy005(float hp,PVector location,PVector direction,int timeout,float angle,boolean bulletRemainFlg,boolean bossFlg){
    super(hp,location,direction,timeout,bulletRemainFlg,bossFlg);
    range = 10.0f;
    //レーザー弾幕生成(敵機位置)
    bulletHellList.add(new LaserLikeBulletHell(this.location,angle));
  }

  //**敵機の位置を更新
  public void updateLocation(){
    location.add(direction);
  }

  //**敵機を描画
  public void draw(){
    if(status == Const.STATUS_ENEMY_ACTIVE){
      draw1();
      activeTime += 1;
    }
  }
 
}

/*------------------------------------------------------------*/
/*敵機タイプ⑥　らせん弾幕　*/
class Enemy006 extends Enemy{

  Enemy006(float hp,PVector location,PVector direction,int timeout,String bulletType,boolean bulletRemainFlg,boolean bossFlg,float angleAdd){
    super(hp,location,direction,timeout,bulletRemainFlg,bossFlg);
    range = 10.0f;
    bulletHellList.add(new HelixBulletHell(this.location,bulletType,angleAdd));
  }

  //**敵機の位置を更新
  public void updateLocation(){
    if(status == Const.STATUS_ENEMY_ACTIVE){
      if(location.y < 200){
        location.add(direction);    
      }
    }
  }
  
  //**敵機を描画
  public void draw(){
    if(status == Const.STATUS_ENEMY_ACTIVE){
      draw1();
      activeTime += 1;
    }
  }  
  
}

/*------------------------------------------------------------*/
/*敵機タイプ⑦　複数らせん弾幕　*/
class Enemy007 extends Enemy{

  Enemy007(float hp,PVector location,PVector direction,int timeout,String bulletType,boolean bulletRemainFlg,boolean bossFlg,
            float angleAdd,int interval,int lineNum,boolean turnFlg,int turnInterval){
    super(hp,location,direction,timeout,bulletRemainFlg,bossFlg);
    range = 10.0f;
    turnInterval = turnFlg ? turnInterval : 1;
    bulletHellList.add(new MultiHelixBulletHell(this.location,bulletType,angleAdd,interval,lineNum,turnFlg,turnInterval));
  }
  
  //**敵機の位置を更新
  public void updateLocation(){
    if(status == Const.STATUS_ENEMY_ACTIVE){
      if(location.y < 300){
        location.add(direction);    
      }
    }
  }

  //**敵機を描画
  public void draw(){
    if(status == Const.STATUS_ENEMY_ACTIVE){
      draw1();
      activeTime += 1;
    }
  }  

}

/*------------------------------------------------------------*/
/*ボス敵機　スペルカード:リニアクリーチャーっぽいやつ　*/
class Boss001 extends Enemy{

  Boss001(float hp,PVector location,PVector direction,int timeout,boolean bulletRemainFlg,boolean bossFlg){
    super(hp,location,direction,timeout,bulletRemainFlg,bossFlg);
    range = 10.0f;
    bulletHellList.add(new WideLaserBulletHell(3,20));
    bulletHellList.add(new FallBulletHell());
  }

  //**敵機の位置を更新
  public void updateLocation(){
    location.add(direction);
  }

  //**敵機を描画
  public void draw(){
    if(status == Const.STATUS_ENEMY_ACTIVE){
      drawBoss();
      activeTime += 1;
    }
  }
  
}

/*------------------------------------------------------------*/
/*ボス敵機　スペルカード:ハート　*/
class Boss002 extends Enemy{

  Boss002(float hp,PVector location,PVector direction,int timeout,boolean bulletRemainFlg,boolean bossFlg){
    super(hp,location,direction,timeout,bulletRemainFlg,bossFlg);
    range = 10.0f;
    bulletHellList.add(new HeartBulletHell(this.location));
  }

  //**敵機の位置を更新
  public void updateLocation(){
    location.add(direction);
  }

  //**敵機を描画
  public void draw(){
    if(status == Const.STATUS_ENEMY_ACTIVE){
      drawBoss();
      activeTime += 1;
    }
  }

}

/*------------------------------------------------------------*/
/*ボス敵機　スペルカード:泳ぐ鳥　*/
class Boss003 extends Enemy{
  
  boolean moveFlg;
  float angle;
  float margin = 30.0f;
  
  Boss003(float hp,PVector location,PVector direction,int timeout,boolean bulletRemainFlg,boolean bossFlg){
    super(hp,location,direction,timeout,bulletRemainFlg,bossFlg);
    range = 10.0f;
    bulletHellList.add(new BoundBulletHell());
    moveFlg = false;
  }
  
  //**敵機の位置を更新
  public void updateLocation(){
    //移動制御
    if(frameCount%120 == 0){
      if(moveFlg){
        direction.x = 0.0f;
        direction.y = 0.0f;
        moveFlg = false;
      }else{
        angle = random(2*PI);
        direction.x = cos(angle)*2;
        direction.y = sin(angle)*2;
        moveFlg = true;
      }
    }
    //画面端に行ったらバウンド(敵機グラもあるので余白を取る)
    if(frameCount%120 != 0){
      if(location.x <= 0 + margin || location.x >= width - margin){
        direction.x *= -1;
      }
      if(location.y <= Const.HEIGHT_INFO + margin || location.y >= 2*height/3){  //あまり下へはいかないように
        direction.y *= -1;
      }
    }
    //位置更新
    location.add(direction);
  }
  
  //**敵機を描画
  public void draw(){
    if(status == Const.STATUS_ENEMY_ACTIVE){
      drawBoss();
      activeTime += 1;
    }
  }
  
}

/*------------------------------------------------------------*/
/*ボス敵機　スペルカード:花型レーザーっぽいやつ　*/
class Boss004 extends Enemy{
  
  Boss004(float hp,PVector location,PVector direction,int timeout,boolean bulletRemainFlg,boolean bossFlg){
    super(hp,location,direction,timeout,bulletRemainFlg,bossFlg);
    range = 10.0f;
    bulletHellList.add(new FlowerLikeLaserBulletHell(5,150));
  }
  
  //**敵機の位置を更新
  public void updateLocation(){
    location.add(direction);
  }
  
  //**敵機を描画
  public void draw(){
    if(status == Const.STATUS_ENEMY_ACTIVE){
      drawBoss();
      activeTime += 1;
    }
  }
  
}
/*ゲームオーバー画面制御*/
class GameOver{
  
  int myFrameCount = 0;
  boolean countFlg = false;
  
  public void drawGameOver(){
    fill(255);
    textSize(30);
    text("Game Over!",50,370);
    
    if(countFlg){
      myFrameCount++;
    }
    
    if(myFrameCount >= 120){
      text("Press Any Key To Retry",100,500);
      textSize(15);
      text("※リトライしない場合、画面を閉じてください",100,550);
      //リトライ
      if(keyPressed){
        countFlg = false;
        myFrameCount = 0;
        retry();
      }
    }
     
  }
  
  public void enableCountFlg(){
    countFlg = true;
  }
  
}
/*画像*/
class Image{
  PImage playerImg;
  PImage bossImg;
  PImage playerCutImg;
  PImage ufoImg;
  
  Image(){
    playerImg = loadImage("graphic/player.png");
    bossImg = loadImage("graphic/boss.png");
    playerCutImg = loadImage("graphic/playerCut.png");
    ufoImg = loadImage("graphic/ufo.png");
  }
  
  //**自機グラ描画
  public void drawPlayerImage(PVector location){
    float x = location.x - Const.PLAYER_GRAPHIC_WIDTH/2;
    float y = location.y - Const.PLAYER_GRAPHIC_HEIGHT*2/3;
    image(playerImg,x,y);
  }
  
  //**ボスグラ描画
  public void drawBossImage(PVector location){
    float x = location.x - Const.BOSS_GRAPHIC_WIDTH/2;
    float y = location.y - Const.BOSS_GRAPHIC_HEIGHT*2/3;
    image(bossImg,x,y);
  }

  //**UFOグラ描画
  public void drawUFOImage(PVector location){
    float x = location.x - Const.UFO_GRAPHIC_WIDTH/2;
    float y = location.y - Const.UFO_GRAPHIC_HEIGHT*2/3;
    image(ufoImg,x,y);
  }

  //**自機グラ(会話シーン)描画
  public void drawPlayerCutImage(PVector location){
    image(playerCutImg,location.x,location.y);
  }
  
  //**ボスグラ(会話シーン)描画
  public void drawBossCutImage(PVector location){
    image(bossImg,location.x,location.y,Const.BOSSCUT_GRAPHIC_WIDTH,Const.BOSSCUT_GRAPHIC_HEIGHT);
  }
 
}
/* レーザークラス(基底) */
abstract class Laser{

  protected PVector startPoint;  //始点ベクトル
  protected PVector endPoint;    //終点ベクトル
  protected PVector velocity;  //速度ベクトル
  protected PVector laserVector; //レーザーベクトル(当たり判定用)
  protected float laserRange;    //レーザー幅
  protected int status;          //ステータス
  protected int moveStatus;      //動作ステータス
  protected int preTime;         //予告状態時間(フレーム数)
  protected int preTimeFinish;   //予告状態終了時間(フレーム数)
  protected int col;           //レーザー色
  
  //**コンストラクタ
  Laser(PVector startPoint,PVector endPoint,PVector velocity,float laserRange,int preTimeFinish,int col){
    
    this.startPoint = startPoint;
    this.endPoint = endPoint;
    if(velocity != null){this.velocity = velocity;}else{this.velocity = new PVector(0.0f,0.0f);}
    this.laserRange = laserRange;
    this.preTimeFinish = preTimeFinish;
    this.status = Const.LASER_STATUS_PRE;
    this.moveStatus = Const.LASER_MOVE_STATUS_STOP;
    this.preTime = 0;
    this.col = col;
    //当たり判定用ベクトル生成
    this.laserVector = (new PVector(this.endPoint.x - this.startPoint.x,this.endPoint.y - this.startPoint.y));
    
  }

  //** レーザーを描画
  public abstract void draw();
  
  
  //**自機への当たり判定
  public boolean isHitToPlayer(Player player){
    /*予告状態の場合スキップ*/
    if(status == Const.LASER_STATUS_PRE){
      return false;
    }
    
    /*当たり判定用レーザーベクトルの更新(回転などで初期生成時から変わっている可能性があるため)*/
    laserVector = (new PVector(endPoint.x - startPoint.x,endPoint.y - startPoint.y));
    
    /*レーザーと自機の距離と、自機当たり半径を比較*/
    
    //レーザーベクトルを正規化
    PVector laserVectorNormal = laserVector.copy();
    laserVectorNormal.normalize();
   
    //レーザーの始点から自機の中心へのベクトルを定義
    PVector vectorB = new PVector(player.getLocation().x - startPoint.x,player.getLocation().y - startPoint.y);
    
    //レーザーと自機の距離を算出
    float distance = vectorB.x * laserVectorNormal.y - laserVectorNormal.x * vectorB.y;  //レーザー中心と自機中心の最短距離を算出(外積)
    distance = Math.abs(distance);    //絶対値化
    distance -= laserRange/2;    //レーザー幅を考慮
    
    //自機半径と比較
    if(distance >= Const.RANGE_HIT_PLAYER){
      return false;
    }
    
    /*自機がレーザーの範囲に入っているか判定*/

    //レーザーの終点から自機の中心へのベクトルを定義
    PVector vectorC = new PVector(player.getLocation().x - endPoint.x,player.getLocation().y - endPoint.y);
    
    //各ベクトルの内積を計算 : いずれかが正、いずれかが負なら範囲に入っている
    float dot1 = vectorB.x * laserVectorNormal.x + vectorB.y * laserVectorNormal.y;
    float dot2 = vectorC.x * laserVectorNormal.x + vectorC.y * laserVectorNormal.y;
    if(dot1 > 0 && dot2 < 0){
      return true;
    }else if(dot1 < 0 && dot2 > 0){
      return true;
    }
    
    /*レーザーの末端付近に当たっているか判定*/
    if(vectorB.mag() < Const.RANGE_HIT_PLAYER || vectorC.mag() < Const.RANGE_HIT_PLAYER){
      return true;
    }
    
    return false;
  }
  
  //**レーザーの状態遷移
  public void updateStatus(int preTimeFinish){
    
    //予告状態の終了判定
    if(status == Const.LASER_STATUS_PRE 
         && preTime >= preTimeFinish
         && preTimeFinish != Const.LASER_PRE_TIME_INVALID){
      status = Const.LASER_STATUS_SHOOT;
    }
    
  }
  
  //**レーザーの位置更新
  public void updateLocation(){
    //※速度ベクトルで動かすときに当処理を使用。それ以外は自前。
    if(moveStatus == Const.LASER_MOVE_STATUS_MOVE){
      startPoint.add(velocity);
      endPoint.add(velocity);
    }
  }
  
  //**画面外に出ているかの判定
  //boolean isOutOfScreen(){
  //  if((startPoint.x < 0 || startPoint.x > width) && (startPoint.y < Const.HEIGHT_INFO || startPoint.y > height)){
  //    if((endPoint.x < 0 || endPoint.x > width) && (endPoint.y < Const.HEIGHT_INFO || endPoint.y > height)){
  //      return true;
  //    }
  //  }
  //  return false;
  //}  
  
  
  /*getter,setter*/
  
  public void setEndPoint(PVector endPoint){
    this.endPoint = endPoint;
  }
  
  public int getStatus(){
    return status;
  }
  
  public void setStatus(int status){
    this.status = status;
  }

  public void setMoveStatus(int moveStatus){
    this.moveStatus = moveStatus;
  }
  
}

/*------------------------------------------------------------*/
/* 通常レーザー */
class NormalLaser extends Laser{
  
  //**コンストラクタ
  NormalLaser(PVector startPoint,PVector endPoint,PVector velocity,float laserRange,int preTimeFinish,int col){
    super(startPoint,endPoint,velocity,laserRange,preTimeFinish,col);
  }
  
  public void draw(){
    /*実ショット*/
    if(status == Const.LASER_STATUS_SHOOT){
      //println("実線");
      strokeWeight(laserRange+7);
      stroke(col,20);
      line(startPoint.x,startPoint.y,endPoint.x,endPoint.y);
      strokeWeight(laserRange+5);
      stroke(col,50);
      line(startPoint.x,startPoint.y,endPoint.x,endPoint.y);
      strokeWeight(laserRange+3);
      stroke(col,60);
      line(startPoint.x,startPoint.y,endPoint.x,endPoint.y);
      strokeWeight(laserRange);
      stroke(col);
      line(startPoint.x,startPoint.y,endPoint.x,endPoint.y);
    }
    /*予告線*/
    if(status == Const.LASER_STATUS_PRE){
      //println("予告線:preTime = " + preTime);
      strokeWeight(laserRange);
      stroke(col,60);
      line(startPoint.x,startPoint.y,endPoint.x,endPoint.y);
      //予告状態制御
      preTime += 1;
      updateStatus(preTimeFinish);
    }
  }
  
}
/*BGM,SE*/

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
  
  public void playBGM(){
    ///bgm1.amp(0.0);
    if(!bgm1.isPlaying()){
      bgm1.loop();
    }
  }
  
  public void playHitPlayer(){
    hitPlayer.amp(0.3f);
    hitPlayer.play();
  }

  public void playDefeteEnemy(){
    defeteEnemy.amp(0.7f);
    defeteEnemy.play();
  }

  public void playGraze(){
    if(!graze.isPlaying()){
      //graze.amp(2.5);
      graze.play();
    }
  }

  public void playExtend(){
    if(!extend.isPlaying()){
      //graze.amp(2.5);
      extend.play();
    }
  }
  
}
/* オープニング画面クラス */
class Opening{
  
  private int count;    //カウントダウン変数
  private int fCount;  //フレームカウント変数
  
  //**コンストラクタ
  Opening(){
    count = 3;
    fCount = 0;
  }
  
  //**オープニングを描画
  public int drawOpening(){
    
    //カウントダウン変数を表示
    int size = 100;
    fill(255);
    textSize(size);
    text(count,width/2-size/2,height/2);
    
    //フレーム数をカウントアップ
    fCount += 1;
    
    //1秒経ったら、カウント変数を1マイナス
    if(fCount%60 == 0){
      count -= 1;
    }
    
    return count;
    
  }
  
  //**カウントダウンをリセット
  public void resetCount(){
    count = 3;
  }
  
}
/*自機クラス*/
class Player{

  private PVector location;  //自機の位置ベクトル
  private PVector direction;  //自機の移動方向ベクトル
  private ArrayList<Integer> directList;  //移動方向リスト
  private float vpf;  //速度(1フレームで何ピクセル進むか)
  
  private int zanki;    //残機
  private int status;    //ステータス
  private int mutekiTime;  //無敵時間
  protected int noMissTime;  //ノーミス時間(フレーム数)
  
  //**コンストラクタ
  Player(float locationX,float locationY){
    
    location = new PVector(locationX,locationY);  //初期位置で自機を生成
    direction = new PVector(0.0f,0.0f);  //初期状態(静止)で移動方向ベクトルを生成
    directList = new ArrayList<Integer>();
    
    vpf = 3.0f;
    zanki = 5;
    status = Const.STATUS_PLAYER_NON;
    mutekiTime = 0;
    noMissTime = 0;
  }
  
  //**自機を描画
  public void draw(){
    
    /*自機位置更新*/
    updateVector();
    location.add(direction);
    
    /*自機描画*/
    stroke(216,52,115);
    strokeWeight(4);
    if(status != Const.STATUS_PLAYER_MUTEKI){
      //通常
      fill(248,244,230);
      noMissTime += 1;
    }else{
      //被弾後無敵状態
      noStroke();
      fill(234,85,80);
    }
    img.drawPlayerImage(location);
    ellipse(location.x,location.y,10,10);
       
    /*ショット描画*/
    if(status == Const.STATUS_PLAYER_SHOOT){
      stroke(255,64);
      strokeWeight(5);
      line(location.x,location.y,location.x,Const.HEIGHT_INFO);
    }
    
  }
  
  /*被弾処理*/
  public void hit(){
    music.playHitPlayer();
    //残機-1
    zanki -= 1;
    //ノーミス時間初期化
    initNoMissTime();
    //無敵時間開始
    if(status != Const.STATUS_PLAYER_MUTEKI){
      status = Const.STATUS_PLAYER_MUTEKI;
    }
  }
  
  /*敵機への衝突処理*/
  public void hitEnemy(Enemy e){
    //残機マイナス
    zanki -= 1;
    //敵機の弾幕を初期化
    e.deleteAllBullet();
    //自機の位置を初期化
    location.x = width/2;
    location.y = height*4/5;
  }
  
  //**移動方向リストに追加
  public void pushDirect(){
    
    int d = 99;
    switch(keyCode){
      case UP:
        d = Const.DIRECTION_UP;
        break;
      case DOWN:
        d = Const.DIRECTION_DOWN;
        break;
      case LEFT:
        d = Const.DIRECTION_LEFT;
        break;
      case RIGHT:
        d = Const.DIRECTION_RIGHT;
        break;
    }
    
    if(d != 99 && directList.indexOf(d) == -1){
      directList.add(d);
    }
    
  }

  //**移動方向リストから削除
  public void popDirect(){
    int index = 0;
    switch(keyCode){
      case UP:
        index = directList.indexOf(Const.DIRECTION_UP);
        if(index != -1){directList.remove(index);}
        break;
      case DOWN:
        index = directList.indexOf(Const.DIRECTION_DOWN);
        if(index != -1){directList.remove(index);}
        break;
      case LEFT:
        index = directList.indexOf(Const.DIRECTION_LEFT);
        if(index != -1){directList.remove(index);}
        break;
      case RIGHT:
        index = directList.indexOf(Const.DIRECTION_RIGHT);
        if(index != -1){directList.remove(index);}
        break;
    }
  }

  //**キー操作によるステータス変更
  public void updateStatusByKey(char key,int keyFlg){
    
    switch(key){
      case Const.KEY_SHOOT:
      status = (keyFlg == Const.KEY_FLG_PRESS ? Const.STATUS_PLAYER_SHOOT : Const.STATUS_PLAYER_NON);
      break;
    }
    
  }
  
  //**時間経過によるステータス制御
  public void updateStatusByTime(){
    
    //無敵時間継続判定
    if(status == Const.STATUS_PLAYER_MUTEKI){
      mutekiTime += 1;
      if(mutekiTime >= Const.MUTEKI_TIME_PLAYER){
        mutekiTime = 0;
        status = Const.STATUS_PLAYER_NON;
      }
    }
    
  }
  
  //**ノーミス時間を初期化
  public void initNoMissTime(){
    noMissTime = 0;
  }
  
  //**エクステンド
  public void extend(){
    if(zanki < Const.ZANKI_MAX){
      zanki += 1;
      music.playExtend();
      //println("Extend! : " + zanki);
    }
  }

  //**初期化
  public void init(){

    location.x = width/2;
    location.y = height*4/5;
    direction.x = 0.0f;
    direction.y = 0.0f;
    directList.clear();
    zanki = 5;
    status = Const.STATUS_PLAYER_NON;
    mutekiTime = 0;
    noMissTime = 0;
    
  }
  

  //**移動方向ベクトルの更新
  private void updateVector(){
    //println("updateVector: directList.size() = " + directList.size()); //<>// //<>//
    
    //どの矢印キーも押されていなければ静止する
    if(directList.size() == 0){
      direction.x = 0.0f;
      direction.y = 0.0f;
      return;
    }
    
    //移動方向リストの末尾の方向に更新
    int d = directList.get(directList.size()-1);
    switch(d){
      case Const.DIRECTION_UP:
        direction.x = 0.0f;
        if(location.y > Const.HEIGHT_INFO){
          direction.y = vpf * -1.0f;
        }else{
          direction.y = 0.0f;
        }
        break;
      case Const.DIRECTION_DOWN:
        direction.x = 0.0f;
        if(location.y < height){
          direction.y = vpf;
        }else{
          direction.y = 0.0f;
        }
        break;
      case Const.DIRECTION_LEFT:
        if(location.x > 0){
          direction.x = vpf * -1.0f;
        }else{
          direction.x = 0.0f;
        }
        direction.y = 0.0f;
        break;
      case Const.DIRECTION_RIGHT:
        if(location.x < width){
          direction.x = vpf;
        }else{
          direction.x = 0.0f;
        }
        direction.y = 0.0f;
        break;
    }
    
  }
  
  //**フィールドのgetter,setter
  //(privateで宣言しているのに、他クラスからアクセスできてしまっているみたいだけど、
  //注意喚起も兼ねてアクセスの際はこちらを使用
  
  public int getStatus(){
    return status;
  }
  
  public int getMutekiTime(){
    return mutekiTime;
  }
  
  public int getZanki(){
    return zanki;
  }
  
  public PVector getLocation(){
    return location;
  }
  
  public int getNoMissTime(){
    return noMissTime;
  }
  
}
/*シナリオ(ストーリー・会話部)*/
class Scenario{
  
  protected int captionProgress;  //キャプション進捗
  protected int captionNum;  //キャプション数
  
  Scenario(){
    captionProgress = 0;
    captionNum = Const.CAPTION_NUM_PRE_BOSS;
  }
  
  public void drawStoryPreBoss(){
    img.drawPlayerCutImage(new PVector(50,height - Const.PLAYERCUT_GRAPHIC_HEIGHT-50));
    img.drawBossCutImage(new PVector(width - Const.BOSSCUT_GRAPHIC_WIDTH-50,height/3));
    if(captionProgress != Const.CAPTION_NUM_PRE_BOSS){
      fill(160);
      textSize(18);
      text("press Enter to next",width-300,height-50);
    }else{
      fill(241,141,158);
      textSize(18);
      text("press Enter to BossStage!!",width-300,height-50);
    }
    switch(captionProgress){
      case 0:
        drawPlayerMessage("あなたは?");
        break;
      case 1:
        drawBossMessage("地球に来たのは久しぶりだ。\nずいぶん荒れ果てたものだね。");
        break;
      case 2:      
        drawPlayerMessage("どこのひと?");
        break;
      case 3:      
        drawBossMessage("月の人。");
        break;
      case 4:      
        drawPlayerMessage("つき、、きょうかしょ　で　みたことがある。\n「くさき　の　いっぽんも　はえない　\n  ひどいほし」だってね");
        break;
      case 5:      
        drawBossMessage("草木しか生えてないような星のやつに\n言われたくないけどね。");
        break;
      case 6:      
        drawBossMessage("月の世界はすべてデータ化して仮想化されている。\n輝く海も、美しい花も、うさぎたちの命も。");
        break;
      case 7:      
        drawPlayerMessage("へえ");
        break;
      case 8:      
        drawBossMessage("………");
        break;
      case 9:      
        drawPlayerMessage("それで　なんのごよう？");
        break;
      case 10:
        drawBossMessage("月からのお迎えだよ。");
        break;
      case 11:
        drawBossMessage("君はほんとうは月の王家の第17王子なのさ。\nだが君はある欠陥を持って生まれてきた。\n「実体がある」という欠陥を。");
        break;
      case 12:
        drawBossMessage("実体があるものは月の世界では生きていけない。\nだから、心苦しいが地球に\n「捨てなきゃいけない」。");
        break;
      case 13:
        drawBossMessage("地球のうさぎは、みんな\n月で捨てられたうさぎたちだよ。");
        break;
      case 14:
        drawPlayerMessage("へええ");
        break;
      case 15:
        drawBossMessage("………はなし聞いてる？");
        break;
      case 16:
        drawBossMessage("とにかくね、\n僕たちは\n君を呼び戻さなくちゃいけない事情ができた。");
        break;
      case 17:
        drawBossMessage("第1王子から第16王子がみんな\nガールフレンドと駆け落ちしたせいで");
        break;
      case 18:
        drawBossMessage("王子様がいなくなっちゃったからね。");
        break;
      case 19:
        drawPlayerMessage("それは　まったく　ろまんちっくだね");
        break;
      case 20:
        drawBossMessage("みんな流行りのドラマに影響されちゃったのさ。\nまったく いい迷惑だよ。");
        break;
      case 21:
        drawPlayerMessage("でも　ぼくは　つきでは　いきられないよ");
        break;
      case 22:
        drawBossMessage("心配ない。君の意識だけ月に来てもらうんだ。");
        break;
      case 23:
        drawPlayerMessage("ぼくを　ころすの？");
        break;
      case 24:
        drawBossMessage("からだのほうは、そうだね。");
        break;
      case 25:
        drawPlayerMessage("それは　こまるよ　\nぼくは　なのはなの　さらだで　\nこれから　ゆうはんに　するんだ");
        break;
      case 26:
        drawBossMessage("仮想世界なら、\n何も食べなくても永遠に生きられるさ。");
        break;
      case 27:
        drawBossMessage("実体から解放された、意識生命体の楽園だよ。");
        break;
    }
    
  }
  
  //**会話シーンを進める
  public void moveToNext(){
    captionProgress += 1;
  }
  
  //**終了判定
  public boolean isFinishScenario(){
    if(captionProgress>captionNum){return true;}
    return false;
  }
  
  private void drawPlayerMessage(String message){
    fill(255);
    textSize(20);
    text(message,50+Const.PLAYERCUT_GRAPHIC_WIDTH+30,height - Const.PLAYERCUT_GRAPHIC_HEIGHT-50);
  }
  private void drawBossMessage(String message){
    fill(255,247,153);
    textSize(20);
    text(message,50,height/3);
  }
  
}
/*スコア*/
static class Score{
  static int score = 0;
  static int graze = 0;
  static int grazeExtend = 0;  //エクステンド判定用のグレイズ(一定数たまったら初期化する)
  static int clearBonus = 0;
  
  //**スコア取得
  public static int getScore(){
    return score;
  }
  //**グレイズ取得
  public static int getGraze(){
    return graze;
  }
  //**エクステンド判定用グレイズ取得
  public static int getGrazeExtend(){
    return grazeExtend;
  }
  //**クリアボーナス取得
  public static int getClearBonus(int zanki){
    if(clearBonus == 0){
      //未計算ならば計算する
      clearBonus = zanki*Const.SCORE_CLEAR;
    }
    return clearBonus;
  }

  //**撃破ボーナス
  public static void addDefeatBonus(Enemy e){
    if(e.getBossFlg()){
      println("ボス撃破ボーナス!");
      score += Const.SCORE_BOSS_DEFEAT;
    }else{
      println("敵機撃破ボーナス!");
      score += Const.SCORE_ENEMY_DEFEAT;
    }
  }
  
  //**ボススペル回避ボーナス
  public static void addSpellClearBonus(){
    println("ボススペル回避成功");
    score += Const.SCORE_BOSS_SPELLCLEAR;
  }
  
  //**一定時間ノー被弾ボーナス
  public static void addNoMissBonus(){
    score += Const.SCORE_NO_MISS;
  }
  
  //**グレイズボーナス
  public static void addGrazeBonus(){
    score += Const.SCORE_GRAZE;
    graze += 1;
    grazeExtend += 1;
  }
  
  //**クリアボーナス(残機数に応じて)
  //static void addClearBonus(int zanki){
  //  clearBonus += zanki*Const.SCORE_CLEAR;
  //}
  
  //エクステンド判定用グレイズの初期化
  public static void initGrazeEntend(){
    grazeExtend = 0;
  }
  
  //**初期化
  public static void init(){
    score = 0;
    graze = 0;
    grazeExtend = 0;
    clearBonus = 0;
  }
}
/*図形の描画用(グローバル関数)*/

/*星型多角形を描く*/
/*ox,oy:中心座標、r:中心点とトゲの頂点までの距離、vertexNum:頂点(星のトゲ)数*/
public void drawStar(float ox,float oy,int r,int vertexNum){

  pushMatrix();
  translate(ox,oy);
  rotate(radians(-90));  //角度をずらして一番上の部分を初期位置とする
  
  beginShape();
  //星を構成する点を1°ずつ定義していって、線分でつなぐ
  for(int theta = 0; theta < 360; theta++){
    PVector pos = calcPosStar(r,theta,vertexNum);
    float x = pos.x;
    float y = pos.y;
    vertex(x,y);
  }
  endShape(CLOSE);
  
  popMatrix();
}

/* 各頂点座標を計算 */
/* r:中心点と頂点間の距離、theta:0～359、num:頂点数*/
public PVector calcPosStar(int r,int theta,int num){
  float x = r * cos(radians(theta)) * funcStar(theta,num);
  float y = r * sin(radians(theta)) * funcStar(theta,num);
  //println("theta = " + theta + " : x = " + x + " : y = " + y);
  return new PVector(x,y);
}

/*theta:0～359、num:頂点数*/
public float funcStar(int theta, int num){
  float a = 360/num;
  float A = cos(radians(a));
  float b = acos(cos(radians(num * theta)));    //acos:逆関数　これを使ってnum*tが360を超えている場合は360の範囲内に収めることができる
  float B = cos(radians(a) - b / num);
  
  return A/B;
}
/* ステージクラス(基底) */
abstract class Stage{

  protected int stageNo;  //ステージ番号
  protected ArrayList<Chapter> chapterList;  //チャプターリスト
  protected boolean initFlg;  //初期化フラグ(false:未、true:済)
    
  Stage(){
    stageNo = 0;
    chapterList = new ArrayList<Chapter>();
    initFlg = false;
  }
  
  //**初期処理
  public void init(){
    //初回チャプターの敵リストを作成
    Chapter ch = chapterList.get(0);
    ch.createEnemy(player);
    initFlg = true;
  }
  
  /*getter,setter*/
  public ArrayList<Chapter> getChapterList(){
    return chapterList;
  }
  
  public boolean getInitFlg(){
    return initFlg;
  }
  
}

/*------------------------------------------------------------*/
/*ステージ1*/
class Stage1 extends Stage{
  
  Stage1(){
    super();
    stageNo = 1;
    //chapterList.add(new BossChapter());
    chapterList.add(new Chapter1());
    chapterList.add(new Chapter2());
    chapterList.add(new Chapter3());
    chapterList.add(new Chapter4());
    chapterList.add(new Chapter5());
    chapterList.add(new Chapter6());
    chapterList.add(new Chapter7());
  }
  
}

/*------------------------------------------------------------*/
/*ステージ2*/
class Stage2 extends Stage{
  
  Stage2(){
    super();
    stageNo = 2;
    chapterList.add(new BossChapter());
  }
  
}
/*タイトル画面描画*/
class Title{
  
  PVector[] location;
  float[] size;
  int[] col;
  
  Title(){
    location = new PVector[6];
    for(int i=0;i<6;i++){
      location[i] = new PVector(0,0);
    }
    size = new float[6];
    col = new int[6];
  }
  
  //**タイトル画面を再生
  public void drawTitle(){
    /*背景描画*/
    
    noStroke();
    /*円状に星を描く*/
    for(int i=0; i<6; i++){
      fill(col[i]);
      pushMatrix();
      //描画位置をランダム生成
      translate(location[i].x,location[i].y);
      //星を描画
      int num = 12;
      for(int j = 0; j < num; j++){
        float ox = 180 * cos(radians(j * 360/num));
        float oy = 180 * sin(radians(j * 360/num));        
        drawStar(ox,oy,floor(size[i]),5); 
      }
      popMatrix();
    }
    
    /*メッセージ描画*/
    fill(255);
    textSize(30);
    text("pless Enter to start...",50,370);
  }
  
  //**初期化(背景描画用)
  public void init(){
    for(int i=0; i<6; i++){
      location[i].x = random(600);
      location[i].y = random(100,750);
      size[i] = random(30,120);
    }
    
    col[0] = color(124,252,0,50);
    col[1] = color(255,228,225,50);
    col[2] = color(0,172,154,50);
    col[3] = color(255,220,0,50);
    col[4] = color(255,255,255,50);
    col[5] = color(234,85,80,50);
    
    //println("");
  }
  
}
//コメント規約

/** ★★★うさぎシューティング★★★ **/

/* クラス説明 */

/* メイン(draw)メソッド内の大項目 */

//**メソッド説明

//通常コメント
  public void settings() {  size(600,750); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "UsagiShooting" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
