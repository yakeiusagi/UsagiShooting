
/*------------------------------------------------------------*/
/*敵機タイプ①　丸型敵機:全方位弾射出*/
class Enemy001 extends Enemy{

  Enemy001(float hp,PVector location,PVector direction,int timeout,String bulletType,boolean bulletRemainFlg,boolean bossFlg){
    super(hp,location,direction,timeout,bulletRemainFlg,bossFlg);
    range = 10.0;
    bulletHellList.add(new AllRoundBullletHell(20,bulletType,this.location));
  }
  
  //**敵機の位置を更新
  void updateLocation(){
    if(status == Const.STATUS_ENEMY_ACTIVE){
      if(location.y < 100){
        location.add(direction);    
      }
    }
  }

  //**敵機を描画
  void draw(){
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
    range = 10.0;
    //自機狙い弾幕生成(敵機位置、自機位置)
    bulletHellList.add(new TargetingBulletHell(this.location,playerLocation));
  }
 
  //**敵機の位置を更新
  void updateLocation(){
    if(status == Const.STATUS_ENEMY_ACTIVE){
      location.add(direction);
    }
  }
  
  //**敵機を描画
  void draw(){
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
    range = 10.0;
    //ランダム水滴弾幕生成(敵機位置)
    bulletHellList.add(new RandomBulletHell(this.location,bulletType,interval));
  }

  //**敵機の位置を更新
  void updateLocation(){
    if(status == Const.STATUS_ENEMY_ACTIVE){
      if(location.y < 100){
        location.add(direction);    
      }
    }
  }
  
  //**敵機を描画
  void draw(){
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
    range = 10.0;
    bulletHellList.add(new AllRoundLaserBulletHell(this.location,3,laserNum,750,rotateFlg));
  }
  
  //**敵機の位置を更新
  void updateLocation(){
    location.add(direction);
  }
  
  //**敵機を描画
  void draw(){
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
    range = 10.0;
    //レーザー弾幕生成(敵機位置)
    bulletHellList.add(new LaserLikeBulletHell(this.location,angle));
  }

  //**敵機の位置を更新
  void updateLocation(){
    location.add(direction);
  }

  //**敵機を描画
  void draw(){
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
    range = 10.0;
    bulletHellList.add(new HelixBulletHell(this.location,bulletType,angleAdd));
  }

  //**敵機の位置を更新
  void updateLocation(){
    if(status == Const.STATUS_ENEMY_ACTIVE){
      if(location.y < 200){
        location.add(direction);    
      }
    }
  }
  
  //**敵機を描画
  void draw(){
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
    range = 10.0;
    turnInterval = turnFlg ? turnInterval : 1;
    bulletHellList.add(new MultiHelixBulletHell(this.location,bulletType,angleAdd,interval,lineNum,turnFlg,turnInterval));
  }
  
  //**敵機の位置を更新
  void updateLocation(){
    if(status == Const.STATUS_ENEMY_ACTIVE){
      if(location.y < 300){
        location.add(direction);    
      }
    }
  }

  //**敵機を描画
  void draw(){
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
    range = 10.0;
    bulletHellList.add(new WideLaserBulletHell(3,20));
    bulletHellList.add(new FallBulletHell());
  }

  //**敵機の位置を更新
  void updateLocation(){
    location.add(direction);
  }

  //**敵機を描画
  void draw(){
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
    range = 10.0;
    bulletHellList.add(new HeartBulletHell(this.location));
  }

  //**敵機の位置を更新
  void updateLocation(){
    location.add(direction);
  }

  //**敵機を描画
  void draw(){
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
  float margin = 30.0;
  
  Boss003(float hp,PVector location,PVector direction,int timeout,boolean bulletRemainFlg,boolean bossFlg){
    super(hp,location,direction,timeout,bulletRemainFlg,bossFlg);
    range = 10.0;
    bulletHellList.add(new BoundBulletHell());
    moveFlg = false;
  }
  
  //**敵機の位置を更新
  void updateLocation(){
    //移動制御
    if(frameCount%120 == 0){
      if(moveFlg){
        direction.x = 0.0;
        direction.y = 0.0;
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
  void draw(){
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
    range = 10.0;
    bulletHellList.add(new FlowerLikeLaserBulletHell(5,150));
  }
  
  //**敵機の位置を更新
  void updateLocation(){
    location.add(direction);
  }
  
  //**敵機を描画
  void draw(){
    if(status == Const.STATUS_ENEMY_ACTIVE){
      drawBoss();
      activeTime += 1;
    }
  }
  
}
