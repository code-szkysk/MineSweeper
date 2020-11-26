package work2;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ExecMineSweeper extends JFrame implements ActionListener{

	int row = 10;	// 横10マス
	int clm = 10;	// 縦10マス
	int endGame = 0;
	int totalBomb = 10;
	int stageNo = 1;

	MineSweeperCell[][] bomb;	// マス目

	ArrayList<MineSweeperCell> shuffleList;


	// コンストラクタ
	public ExecMineSweeper() {
		// Xボタンで閉じる
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(500,500);
		// コンポーネント配置領域を取得
		Container con = this.getContentPane();


		// コンテナの設置 10 x 10 のグリッドレイアウト
		con.setLayout(new GridLayout(this.row, this.clm));

		// コンポーネントの設定
		this.bomb = new MineSweeperCell[this.row][this.clm];
		this.shuffleList = new ArrayList<>();

		// 2次元配列の中身のMineSweeperCellインスタンスを生成
		for(int i=0; i<this.row; i++) {
			for(int j=0; j<this.clm; j++) {
				this.bomb[i][j] = new MineSweeperCell();
				this.bomb[i][j].addActionListener(this);

				// 爆弾抽選用のリストにも入れておく
				this.shuffleList.add(this.bomb[i][j]);

				// コンテナに配置
				con.add(bomb[i][j]);
			}
		}

		this.initialize();
	}

	// ゲーム開始時に初期化されるメソッド
	public void initialize() {

		this.endGame = 0;

		// 爆弾の数がマス目の数を超えないように調整
		if(this.totalBomb >= this.row * this.clm) {
			this.totalBomb = (this.row * this.clm) -1;
		}

		// フレームのタイトルに変数を使う
		this.setTitle("ステージ" + this.stageNo + "：" + "爆弾数" + this.totalBomb);

		// 全てのマスを初期化
		for(int i=0; i<this.row; i++) {
			for(int j=0; j<this.clm; j++) {
				this.bomb[i][j].initialize();
			}
		}

		this.setBomb();
	}

	// 爆弾を設置するマスを抽選で設定
	private void setBomb() {
		Collections.shuffle(shuffleList);

		for(int i=0; i<this.totalBomb; i++) {
			MineSweeperCell cell = this.shuffleList.get(i);

			cell.setOnBomb();	// このマスを爆弾として指定
			//cell.setBackground(Color.ORANGE);	// 動作チェックとして背景に色付け
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		for(int i=0; i<this.row; i++) {
			for(int j=0; j<this.clm; j++) {
				if(e.getSource() == this.bomb[i][j]) {
					//System.out.println(i + "-" + j + "クリックされた");

					this.clickedButton(i,j);
					return;	// for文を繰り返さないためのreturn
				}
			}
		}
	}

	public void clickedButton(int r, int c) {

		// クリック済のマスではない場合のみ動作する
		if(!this.bomb[r][c].isCheckOpen()) {
			if(this.bomb[r][c].isBomb()) {	// 爆弾マスの判定チェック
				this.endGame = 1;
			}else {
				this.searchAroundBomb(r,c);
				this.endGame = this.gameClearCheck();
			}

			// endGameの値が0より大きい場合はゲーム終了
			if(this.endGame > 0) {
				this.gameOver();
			}
		}
	}

	public void gameOver() {

		for(int i=0; i<this.row; i++) {	// 爆弾マスを爆発させる
			for(int j=0; j<this.clm; j++) {
				bomb[i][j].bomber();
			}
		}

		// ダイアログウィンドウを表示
		String msg = "lose";
		String msgTitle = "Game Over";

		// プレイヤー勝利ならメッセージ変更

		if(this.endGame == 2) {
			msg = "win";
			msgTitle = "Clear";

			// さらに次ゲームのステージ数や爆弾数を変更する
			this.stageNo ++;
			this.totalBomb += 2;	// 爆弾２個追加
		}

		JOptionPane.showMessageDialog(this,msg,msgTitle,JOptionPane.INFORMATION_MESSAGE);

		this.initialize();// 次ゲーム開始
	}

	// 爆弾マス以外が全て開けば勝利
	public int gameClearCheck() {

		int gcc = 0;	// 開いていないマス

		for(int i=0; i<this.row; i++) {	// 全マスを対象
			for(int j=0; j<this.clm; j++) {
				if(!bomb[i][j].isCheckOpen()) {
					gcc++;
				}
			}
		}

		if(gcc == this.totalBomb) {	// 開いていないマスと爆弾数が同じならば
			return 2;
		}else {
			return 0;
		}
	}

	public void searchAroundBomb(int r, int c) {

		int sab = 0;
		for(int i=-1; i<=1; i++) {	// 周囲の8マスを対象とする２重for文
			for(int j=-1; j<=1; j++) {

				// 自身のポイント座標は除く i=0 j=0 じゃない場合
				if(!(i==0 && j==0) && this.checkOKSearch(r+i,c+j)) {
					if(bomb[r+i][c+j].isBomb()) {
						sab++;	// 爆弾ならば +1
					}
				}
			}
		}
	}

	// 対象となるマスの周囲に爆弾があるか再帰的にチェック
	public void searchNextSpace(int r, int c) {

		for(int i=-1; i<=1; i++) {
			for(int j=-1; j<=1; j++) {
				if(!(i==0 && j==0) && this.checkOKSearch(r+i, c+j)) {

					// 開いていないマスなら爆弾数をかぞえる
					if(!bomb[r+i][c+j].isCheckOpen()) {
						this.searchAroundBomb(r+i, c+j);
					}
				}
			}
		}
	}

	public boolean checkOKSearch(int r, int c) {
		// row と clm が0以上ならば
		if((0 <= r && r < this.row) && (0 <= c && c < this.clm)) {
			return true;
		}
		return false;
	}


	public static void main(String[] args) {
		ExecMineSweeper frame = new ExecMineSweeper();
		frame.setVisible(true);
	}

}