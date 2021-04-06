
/* 弾クラス(基底) */
abstract class Bullet{
  protected PVector location;  //位置ベクトル
  protected PVector velocity;  //速度ベクトル
  protected PVector gravity;   //重力ベクトル
  protected color col;        //弾幕色
  protected float range;    //弾の当たり判定半径
  protected int boundNum;    //バウンド回数
  
  //** コンストラクタ
  Bullet(PVector location,PVector velocity,PVector gravity,color col){
    this.location = location;
    this.velocity = velocity;
    if(gravity != null){
      this.gravity = gravity;
    }else{
      this.gravity = new PVector(0.0,0.0);  //重力不使用
    }
    this.col = col;
    this.range = 0.0;
    this.boundNum = 0;
  }
  
  //** 弾を描画
  abstract void draw();

  //** 弾の位置を更新
  void updateLocation(){
    location.add(velocity);  //<>//
    location.add(gravity);
  }
  
  //**自機への当たり判定
  boolean isHitToPlayer(Player player){
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
  abstract boolean isGrazeToPlayer(Player player);
  
  boolean isGrazeToPlayerCommon(Player player,float grazeRange){
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
  boolean isOutOfScreen(){
    if(location.x < 0 || location.x > width){
      return true;
    }
    if(location.y < 0 || location.y > height){
      return true;
    }
    return false;
  }

  //**弾のバウンド回数カウントアップ
  void countBoundNum(){
    boundNum += 1;
  }  

  /*getter,setter*/
  PVector getLocation(){
    return location;
  }
  PVector getVelocity(){
    return velocity;
  }  
  void setVelocity(PVector velocity){
    this.velocity = velocity;
  }
  int getBoundNum(){
    return boundNum;
  }
  
}

/*------------------------------------------------------------*/
/* 小弾クラス */
class SmallBullet extends Bullet{
  
  //** コンストラクタ
  SmallBullet(PVector location,PVector velocity,PVector gravity,color col){
    super(location,velocity,gravity,col);
    this.range = Const.SMALLBULLET_RANGE;
  }
  
  //** 弾を描画
  void draw(){
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
  boolean isGrazeToPlayer(Player player){
    return isGrazeToPlayerCommon(player,Const.SMALLBULLET_GRAZE);
  }
  
}

/*------------------------------------------------------------*/
/* 大弾クラス */
class LargeBullet extends Bullet{
  
  //** コンストラクタ
  LargeBullet(PVector location,PVector velocity,PVector gravity,color col){
    super(location,velocity,gravity,col);
    this.range = Const.LARGEBULLET_RANGE;
  }
  
  //** 弾を描画
  void draw(){
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
  boolean isGrazeToPlayer(Player player){
    return isGrazeToPlayerCommon(player,Const.LARGEBULLET_GRAZE);
  }
  
}

/*------------------------------------------------------------*/
/* 三角弾クラス */
/* メモ:描画はイラストにして、実際の当たり判定は円形*/
class TriangleBullet extends Bullet{

  //** コンストラクタ
  TriangleBullet(PVector location,PVector velocity,PVector gravity,color col){
    super(location,velocity,gravity,col);
    this.range = 10;
  }
  
  //** 弾を描画
  void draw(){
    fill(col);
  }
  
  //** グレイズ判定
  boolean isGrazeToPlayer(Player player){
    //return isGrazeToPlayerCommon(player,Const.LARGEBULLET_GRAZE);
    return false;
  }

}
