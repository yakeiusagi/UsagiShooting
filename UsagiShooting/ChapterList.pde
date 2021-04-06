/*------------------------------------------------------------*/
/*チャプター1*/
class Chapter1 extends Chapter{
  
  Chapter1(){
    super();
    chapterNo = 1;
  }

  //**敵を生成
  void createEnemy(Player player){
    //全方位弾敵*3
    enemyList.add(new Enemy001(60.0,new PVector(width/2,0.0),new PVector(0.0,2.0),240,Const.BULLET_TYPE_SMALL,true,false));
    enemyList.add(new Enemy001(60.0,new PVector(width/4,0.0),new PVector(0.0,2.0),240,Const.BULLET_TYPE_SMALL,true,false));
    enemyList.add(new Enemy001(60.0,new PVector(3*width/4,0.0),new PVector(0.0,2.0),240,Const.BULLET_TYPE_SMALL,true,false));
  }
  
  //**チャプターシナリオを実行
  void exec(Player player){

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
  void createEnemy(Player player){
    //自機狙い弾敵*3
    enemyList.add(new Enemy002(Const.HP_ENEMY_INVALID,new PVector(0.0,100.0),new PVector(3.0,0.0),player.getLocation(),Const.TIMEOUT_ENEMY_INVALID,true,false));
    enemyList.add(new Enemy002(Const.HP_ENEMY_INVALID,new PVector(width,100.0),new PVector(-3.0,0.0),player.getLocation(),Const.TIMEOUT_ENEMY_INVALID,true,false));
    enemyList.add(new Enemy002(Const.HP_ENEMY_INVALID,new PVector(0.0,100.0),new PVector(3.0,0.0),player.getLocation(),Const.TIMEOUT_ENEMY_INVALID,true,false));
    //ランダム弾*3
    enemyList.add(new Enemy003(60.0,new PVector(width/2,0.0),new PVector(0.0,2.0),240,Const.BULLET_TYPE_LARGE,true,false,6));
    enemyList.add(new Enemy003(60.0,new PVector(width/4,0.0),new PVector(0.0,2.0),240,Const.BULLET_TYPE_LARGE,true,false,6));
    enemyList.add(new Enemy003(60.0,new PVector(3*width/4,0.0),new PVector(0.0,2.0),240,Const.BULLET_TYPE_LARGE,true,false,6));
  }

  //**チャプターシナリオを実行
  void exec(Player player){

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
  void createEnemy(Player player){
    //放射レーザー*2
    enemyList.add(new Enemy004(
                        Const.HP_ENEMY_INVALID
                        ,new PVector(width/8,100.0)
                        ,new PVector(0.0,0.0)
                        ,16
                        ,Const.LASER_ROTATE_COUNTER_CLOCKWISE
                        ,180
                        ,false
                        ,false));
    enemyList.add(new Enemy004(
                        Const.HP_ENEMY_INVALID
                        ,new PVector(7*width/8,100.0)
                        ,new PVector(0.0,0.0)
                        ,16
                        ,Const.LASER_ROTATE_CLOCKWISE
                        ,180
                        ,false
                        ,false));
  }
  
  //**チャプターシナリオを実行+
  void exec(Player player){
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
  void createEnemy(Player player){
    //放射レーザー
    enemyList.add(new Enemy004(
                        Const.HP_ENEMY_INVALID
                        ,new PVector(width/2,100.0)
                        ,new PVector(0.0,0.0)
                        ,9
                        ,Const.LASER_ROTATE_OFF
                        ,600
                        ,false
                        ,false));
    //ランダム弾*2                    
    enemyList.add(new Enemy003(180.0,new PVector(width/4,0.0),new PVector(0.0,2.0),600,Const.BULLET_TYPE_SMALL,true,false,3));
    enemyList.add(new Enemy003(180.0,new PVector(3*width/4,0.0),new PVector(0.0,2.0),600,Const.BULLET_TYPE_SMALL,true,false,3));
  }
  
  //**チャプターシナリオを実行
  void exec(Player player){
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
  void createEnemy(Player player){
    for(int i=0; i<20; i++){
      //レーザー弾幕
      enemyList.add(new Enemy005(Const.HP_ENEMY_INVALID,new PVector(random(width),random(Const.HEIGHT_INFO,250)),new PVector(0,1),60,PI/2,true,false));
    }
  }
  
  //**チャプターシナリオを実行
  void exec(Player player){
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
  void createEnemy(Player player){
    enemyList.add(new Enemy006(90,new PVector(width/2,0.0),new PVector(0,2),480,Const.BULLET_TYPE_SMALL,true,false,radians(22)));
    enemyList.add(new Enemy006(90,new PVector(width/4,0.0),new PVector(0,2),480,Const.BULLET_TYPE_SMALL,true,false,radians(22)));
    enemyList.add(new Enemy006(90,new PVector(3*width/4,0.0),new PVector(0,2),480,Const.BULLET_TYPE_SMALL,true,false,radians(22)));
    //複数らせん(角度増分3°、発射間隔1、放射数5、転換あり、転換間隔20
    //enemyList.add(new Enemy007(90,new PVector(width/2,100.0),new PVector(0,2),Const.TIMEOUT_ENEMY_INVALID,Const.BULLET_TYPE_SMALL,true,false,radians(3),1,4,true,20));
  }
  
  //**チャプターシナリオを実行
  void exec(Player player){
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
  void createEnemy(Player player){
    //複数らせん(角度増分2°、発射間隔6、放射数5、転換なし）
    enemyList.add(new Enemy007(90,new PVector(width/2,100.0),new PVector(0,2),360,Const.BULLET_TYPE_SMALL,true,false,radians(2),12,5,false,1));
    //全方位弾
    enemyList.add(new Enemy001(60.0,new PVector(width/5,0.0),new PVector(0.0,2.0),240,Const.BULLET_TYPE_SMALL,true,false));
    enemyList.add(new Enemy001(60.0,new PVector(4*width/5,0.0),new PVector(0.0,2.0),240,Const.BULLET_TYPE_SMALL,true,false));
  }

  //**チャプターシナリオを実行
  void exec(Player player){
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
  void createEnemy(Player player){
    enemyList.add(new Boss001(1200,new PVector(width/2,100),new PVector(0,0),3600,false,true));
    enemyList.add(new Boss002(300,new PVector(width/2,250),new PVector(0,0),1800,false,true));
    enemyList.add(new Boss003(600,new PVector(width/2,100),new PVector(0,0),3600,false,true));
    enemyList.add(new Boss004(2400,new PVector(width/2,100),new PVector(0,0),3600,false,true));
  }
  
  //**チャプターシナリオを実行+
  void exec(Player player){
    execNormalSenario(player);
  }  
  
}
