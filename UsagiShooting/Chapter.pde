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
  abstract void createEnemy(Player player);
  
  //**チャプターシナリオを実行
  abstract void exec(Player player);

  
  //**チャプターが終了したか判定
  boolean isChapterEnd(){
    for(Enemy e : enemyList){
      //処理中の敵がある場合、未終了
      if(e.getStatus() != Const.STATUS_ENEMY_DONE){ return false; }
    }
    return true;
  }
  
  //**アクティブ状態の敵が存在するか判定
  boolean isExistActiveEnemy(){
    for(Enemy e : enemyList){
      if(e.getStatus() == Const.STATUS_ENEMY_ACTIVE){ return true; }
    }
    return false;
  }
  
  //**基本シナリオ
  void execBaseSenario(Enemy e,Player player){
    /*敵機、弾幕の描画*/
    e.updateLocation();
    e.draw();
    e.drawBulletHell();
    
    /*敵機への攻撃・撃破判定*/
    e.judgeHitToEnemy(player);
    if(e.isDefeat()){
      println("敵機撃破");
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
      println("敵機タイムアウト");
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
  void execNormalSenario(Player player){
    
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
  void execAtOnceSenario(Player player){
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
  void execConstantSenario(Player player,int interval){
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
