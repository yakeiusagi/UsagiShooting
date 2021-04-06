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
color bgCol;
GameOver gameOver;

void setup(){
  
  size(600,750);
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

void draw(){
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

void keyPressed(){
  /* 自機移動方向制御 */
  player.pushDirect();
  /* 自機ステータス制御 */
  player.updateStatusByKey(key,Const.KEY_FLG_PRESS);  
}

void keyReleased(){
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
void execGame(Stage stage){
  
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
    
    println("chapter end");

    //終了したチャプターはリストから削除
    chapterList.remove(0);
   
    //全てのチャプターが終了した場合は次のシーンに進む
    if(chapterList.size() == 0){
      println("全チャプター終了");
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
void drawTitle(){
  title.drawTitle();  
  if(keyPressed && (key==ENTER || key==RETURN)){
    sceneNo = Const.SCENE_NO_OPENING;
  }
}

//**リトライ処理
void retry(){
  op.resetCount();
  stageList.clear();
  stageList.add(new Stage1());
  stageList.add(new Stage2());
  player.init();
  Score.init();
  sceneNo = Const.SCENE_NO_OPENING;
}

//**リザルト画面を再生
void drawResult(){
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
void drawStoryPreBoss(){
  
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
void drawInfo(){
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
