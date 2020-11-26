package work2;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;

public class MineSweeperCell extends JButton{

	private boolean bomb; // 爆弾があるマスかどうか
	private boolean checkOpen; // クリック済みのマスかどうか


	public MineSweeperCell() {
		// フォントの指定メソッド
		this.setFont(new Font(Font.DIALOG,Font.BOLD,20));
		this.initialize();	// 初期化メソッド
	}


	public void initialize() {

		this.bomb = false;
		this.checkOpen = false;

		this.setText("");
		this.setBackground(null);
		this.setForeground(Color.black);
	}

	public void setOnBomb() {
		this.bomb = true;	// 爆弾をセット
	}

	public boolean isBomb() {

		return this.bomb;
	}

	public boolean isCheckOpen() {
		return this.checkOpen;	// マス目をひらく
	}

	public void setAroundBomb(int num) {
		this.checkOpen = true;
		this.setBackground(Color.white);

		if(num > 0) {
			this.setText(String.valueOf(num));
		}
	}

	public void bomber() {
		if(this.bomb) {
			this.setForeground(Color.BLUE);
			this.setText("B");
		}
	}

}