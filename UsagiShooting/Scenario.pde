/*シナリオ(ストーリー・会話部)*/
class Scenario{
  
  protected int captionProgress;  //キャプション進捗
  protected int captionNum;  //キャプション数
  
  Scenario(){
    captionProgress = 0;
    captionNum = Const.CAPTION_NUM_PRE_BOSS;
  }
  
  void drawStoryPreBoss(){
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
  void moveToNext(){
    captionProgress += 1;
  }
  
  //**終了判定
  boolean isFinishScenario(){
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
