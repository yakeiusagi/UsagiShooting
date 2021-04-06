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
  void init(){
    //初回チャプターの敵リストを作成
    Chapter ch = chapterList.get(0);
    ch.createEnemy(player);
    initFlg = true;
  }
  
  /*getter,setter*/
  ArrayList<Chapter> getChapterList(){
    return chapterList;
  }
  
  boolean getInitFlg(){
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
