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
  abstract void draw(int status);
  
  //**描画処理
  void drawBulletHell(){
    
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
  boolean isHitToPlayer(Player player){
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
  int calcGrazeNum(Player player){
    int grazeNum = 0;
    for(Bullet b : bulletList){
      if(b.isGrazeToPlayer(player)){
        grazeNum += 1;
      }
    }
    return grazeNum;
  }
  
  //**画面外に出た弾を消去
  void deleteBullet(){
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
  void deleteAllBullet(){
    bulletList.clear();
    laserList.clear();
  }

  /*getter,setter*/
  ArrayList<Bullet> getBulletList(){
    return bulletList;
  }
  ArrayList<Laser> getLaserList(){
    return laserList;
  }
  
}
