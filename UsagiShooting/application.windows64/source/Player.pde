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
    direction = new PVector(0.0,0.0);  //初期状態(静止)で移動方向ベクトルを生成
    directList = new ArrayList<Integer>();
    
    vpf = 3.0;
    zanki = 5;
    status = Const.STATUS_PLAYER_NON;
    mutekiTime = 0;
    noMissTime = 0;
  }
  
  //**自機を描画
  void draw(){
    
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
  void hit(){
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
  void hitEnemy(Enemy e){
    //残機マイナス
    zanki -= 1;
    //敵機の弾幕を初期化
    e.deleteAllBullet();
    //自機の位置を初期化
    location.x = width/2;
    location.y = height*4/5;
  }
  
  //**移動方向リストに追加
  void pushDirect(){
    
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
  void popDirect(){
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
  void updateStatusByKey(char key,int keyFlg){
    
    switch(key){
      case Const.KEY_SHOOT:
      status = (keyFlg == Const.KEY_FLG_PRESS ? Const.STATUS_PLAYER_SHOOT : Const.STATUS_PLAYER_NON);
      break;
    }
    
  }
  
  //**時間経過によるステータス制御
  void updateStatusByTime(){
    
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
  void initNoMissTime(){
    noMissTime = 0;
  }
  
  //**エクステンド
  void extend(){
    if(zanki < Const.ZANKI_MAX){
      zanki += 1;
      music.playExtend();
      //println("Extend! : " + zanki);
    }
  }

  //**初期化
  void init(){

    location.x = width/2;
    location.y = height*4/5;
    direction.x = 0.0;
    direction.y = 0.0;
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
      direction.x = 0.0;
      direction.y = 0.0;
      return;
    }
    
    //移動方向リストの末尾の方向に更新
    int d = directList.get(directList.size()-1);
    switch(d){
      case Const.DIRECTION_UP:
        direction.x = 0.0;
        if(location.y > Const.HEIGHT_INFO){
          direction.y = vpf * -1.0;
        }else{
          direction.y = 0.0;
        }
        break;
      case Const.DIRECTION_DOWN:
        direction.x = 0.0;
        if(location.y < height){
          direction.y = vpf;
        }else{
          direction.y = 0.0;
        }
        break;
      case Const.DIRECTION_LEFT:
        if(location.x > 0){
          direction.x = vpf * -1.0;
        }else{
          direction.x = 0.0;
        }
        direction.y = 0.0;
        break;
      case Const.DIRECTION_RIGHT:
        if(location.x < width){
          direction.x = vpf;
        }else{
          direction.x = 0.0;
        }
        direction.y = 0.0;
        break;
    }
    
  }
  
  //**フィールドのgetter,setter
  //(privateで宣言しているのに、他クラスからアクセスできてしまっているみたいだけど、
  //注意喚起も兼ねてアクセスの際はこちらを使用
  
  int getStatus(){
    return status;
  }
  
  int getMutekiTime(){
    return mutekiTime;
  }
  
  int getZanki(){
    return zanki;
  }
  
  PVector getLocation(){
    return location;
  }
  
  int getNoMissTime(){
    return noMissTime;
  }
  
}
