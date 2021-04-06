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
  final static float HEIGHT_INFO = 50.0;

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
  final static float RANGE_HIT_PLAYER = 5.0;
  
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
  final static float SMALLBULLET_DIAMETER = 10.0;  //小弾直径
  final static float SMALLBULLET_RANGE = 3.0;  //小弾当たり判定半径
  final static float SMALLBULLET_GRAZE = 15.0;  //小弾グレイズ判定半径
  final static float LARGEBULLET_DIAMETER = 30.0;  //大弾直径
  final static float LARGEBULLET_RANGE = 10.0;  //大弾当たり判定半径
  final static float LARGEBULLET_GRAZE = 25.0;  //大弾グレイズ判定半径
  
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
  final static float PLAYER_GRAPHIC_WIDTH = 35.0;
  final static float PLAYER_GRAPHIC_HEIGHT = 53.0;
  
  //ボスグラフィック
  final static float BOSS_GRAPHIC_WIDTH = 50.0;
  final static float BOSS_GRAPHIC_HEIGHT = 82.0;
  final static float BOSS_HPGAUGE_DIAMETER = 90.0;

  //UFOグラフィック
  final static float UFO_GRAPHIC_WIDTH = 60.0;
  final static float UFO_GRAPHIC_HEIGHT = 56.0;
  final static float UFO_HPGAUGE_DIAMETER = 90.0;

  //自機カットグラフィック
  final static float PLAYERCUT_GRAPHIC_WIDTH = 100.0;
  final static float PLAYERCUT_GRAPHIC_HEIGHT = 150.0;

  //ボスカットグラフィック
  final static float BOSSCUT_GRAPHIC_WIDTH = 100.0;
  final static float BOSSCUT_GRAPHIC_HEIGHT = 163.0;
 
 //会話
 final static int CAPTION_NUM_PRE_BOSS = 27;
 
  //背景色（グレイスケール）
  final static int BACKGROUND_COLOR_NORMAL = 0;
  final static int BACKGROUND_COLOR_BLACKOUT = 25;
}
