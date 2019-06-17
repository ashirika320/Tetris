package Tetris;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Tetris extends JPanel{
	private Tetromino nextone;         //��һ���������
	private Tetromino tetromino;       //��ǰ�������
	private static final int ROWS=20;  //��
	private static final int COLS=10;  //��
	
	private int score=0;      //����
	private int lines = 0;    //��������
	private int level = 5;    //�ȼ�

	
	
	private Cell[][]wall=new Cell[ROWS][COLS];   //ǽ
	
	private boolean STATE=true;      //STATE����״̬
	
	/*
	 * ���ӳ��ȵĻ���
	 */
	public static final int CELL_SIZE=26;
	public static BufferedImage Z;
	public static BufferedImage S;
	public static BufferedImage J;
	public static BufferedImage L;
	public static BufferedImage D;
	public static BufferedImage I;
	public static BufferedImage T;
	public static BufferedImage bgi;
	public static BufferedImage pause;   
	public static BufferedImage tetris;
	public static BufferedImage gameover;
	static {
		try {
			Z=ImageIO.read(Tetris.class.getResource("../Image/Z.png"));
			S=ImageIO.read(Tetris.class.getResource("../Image/S.png"));
			J=ImageIO.read(Tetris.class.getResource("../Image/J.png"));
			L=ImageIO.read(Tetris.class.getResource("../Image/L.png"));
			D=ImageIO.read(Tetris.class.getResource("../Image/D.png"));
			I=ImageIO.read(Tetris.class.getResource("../Image/I.png"));
			T=ImageIO.read(Tetris.class.getResource("../Image/T.png"));
			
			pause=ImageIO.read(Tetris.class.getResource("../Image/pause.png"));
			tetris=ImageIO.read(Tetris.class.getResource("../Image/tetris1.png"));
			gameover=ImageIO.read(Tetris.class.getResource("../Image/gameover.png"));
			bgi=ImageIO.read(Tetris.class.getResource("../Image/bgi.png"));
			
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void action() {
		tetromino=Tetromino.ranShape();
		nextone=Tetromino.ranShape();
		
		KeyListener k1=new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				int k=e.getKeyCode();
				keyMoveAction(k);
				repaint();
			}
		};
		//Tetris���������1 ���� ���Ķ���
		this.addKeyListener(k1);
		this.setFocusable(true);
		this.requestFocus();//���󽹵�
		
		Timer timer=new Timer();
		TimerTask task=new TimerTask() {
			int moveIndex=0;
			@SuppressWarnings("unused")
			int bgiIndex=0;
			int speed=5*level;
			//��ʱ����ʱִ�в���
			public void run() {
				if(STATE) {
					if(moveIndex%speed==0) {
						
						moveDownAction();
						moveIndex=0;
					}
				}
				moveIndex++;
				bgiIndex++;
				repaint();
				
			}
		};
		//��ʱ���Ķ�ʱִ��װ��
		timer.schedule(task, 10,20);
	}
	
	
	
	public void testAction() {
		for(int i=0;i<tetromino.cells.length;i++) {
			System.out.println(tetromino.cells[i].getRow()+" "+tetromino.cells[i].getCol());
		}
	}
	
	public void keyMoveAction(int k) {
		switch(k) {
		case KeyEvent.VK_RIGHT:
			moveRightAction();
			break;
		case KeyEvent.VK_LEFT:
			moveLeftAction();
			break;
		case KeyEvent.VK_DOWN:
			moveDownAction();
			break;
		case KeyEvent.VK_UP:
			spinCellAction();
			break;
		case KeyEvent.VK_I:
			moveInitAction();
			break;
		case KeyEvent.VK_P:   
			STATE=false;
			break;
		case KeyEvent.VK_C:
			STATE=true;
			break;
		case KeyEvent.VK_E:
			System.exit(0);
			break;
		
		}
	}
	/*
	 * ��ʼ������
	 */
	public void moveInitAction() {
		STATE=false;
		wall=new Cell[ROWS][COLS];
		tetromino=Tetromino.ranShape();
		nextone=Tetromino.ranShape();
		score=0;
		lines=0;
		level=0;
	}
	/*
	 * ��ת����
	 */
	public void spinCellAction() {
		Cell[] nCells=tetromino.spin();
		if(nCells==null)
			return;
		for(int i=0;i<nCells.length;i++) {
			int nRow=nCells[i].getRow();
			int nCol=nCells[i].getCol();
			
			if(nRow<0||nRow>=ROWS||nCol<0||nCol>=COLS||wall[nRow][nCol]!=null)
				return;
			
		}
		tetromino.cells=nCells;
	}
	/*
	 * ���Ʒ���
	 */
	public void moveLeftAction() {
		if(canLeftMove()&&!isBottom()) {
			tetromino.moveLeft();
		}
	}
	/*
	 * ���Ʒ���
	 */
	public void moveRightAction() {
		if(canRightMove()&&!isBottom()) {
			tetromino.moveRight();
		}
	}
	/*
	 * ���䷽��
	 */
	public void moveDownAction() {
		if(tetromino==null)
			return;
		if(!isBottom()) {
			tetromino.moveDown();
		}
	}
	/*
	 * 
	 */
	public void removeLine() {
		boolean flag=true;
		int rowStart=20;
		for(int row=0;row<ROWS;row++) {
			for(int col=0;col<COLS;col++) {
				if(wall[row][col]==null) {
					flag=false;
					break;
				}
			}
			if(flag) {
				for(int col=0;col<COLS;col++) {
					wall[row][col]=null;
				}
				rowStart=row;
				score+=10;
				lines+=1;
				level=lines%10==0?level==1?level:level-1:level;
				
				for(int row1=rowStart;row1>0;row1--) {
					for(int col1=0;col1<COLS;col1++) {
						wall[row1][col1]=wall[row1-1][col1];
						
					}
				}
			}else {
				flag=true;
			}
		}
	}
	/*
	 * �Ƿ���Լ�������
	 */
	public boolean isBottom() {
		if(tetromino==null)
			return false;
		Cell[] cells=tetromino.cells;
		for(int i=0;i<cells.length;i++) {
			Cell c=cells[i];
			int col=c.getCol();
			int row=c.getRow();
			
			if((row+1)==ROWS||wall[row+1][col]!=null) {
				//��ȷ����ǰ�����˶����ײ���ֹͣʱ�����ö���cells�ڵĸ���Ԫ�ش���wall��
				for(int j=0;j<cells.length;j++) {
					Cell cell=cells[j];
					int col1=cell.getCol();
					int row1=cell.getRow();
					wall[row1][col1]=cell;
					
				}
				removeLine();
				
				tetromino=nextone;
				nextone=Tetromino.ranShape();
				
				return true;
			}
		}
		return false;
	}
	/*
	 * �ж��Ƿ��������
	 */
	public boolean canRightMove() {
		if(tetromino==null)
			return false;
		Cell[] cells=tetromino.cells;
		for(int i=0;i<cells.length;i++) {
			Cell c=cells[i];
			int row=c.getRow();
			int col=c.getCol();
			if((col+1)==COLS||wall[row][col+1]!=null) {
				return false;
			}
		}
			return true;
		}
		
	/*
	 * �ж��Ƿ��������
	  */		
	public boolean canLeftMove() {
		if(tetromino==null)
			return false;
		Cell[] cells=tetromino.cells;
		for(int i=0;i<cells.length;i++) {
			Cell c=cells[i];
			int row=c.getRow();
			int col=c.getCol();
			if(col==0||wall[row][col-1]!=null)
				return false;
		}
		return true;
			
	}
	
	public void paint(Graphics g) {
		g.drawImage(bgi, 0, 0, null);
		g.drawImage(tetris, 0, 0, null);
		g.translate(15,15);
			
		paintWall(g);       //��ǽ
		paintTetromino(g);  //����ǰ�������
		paintNextone(g);    //��Ԥ���������
		paintTabs(g);       //����ʾ��Ϣ��score,lines,level
		paintGameOver(g);   //����Ϸ��������
			
	}
	/*
	 * ��Ϸ����
	 */
	public boolean isGameOver() {
		for(int col=0;col<COLS;col++) {
			if(wall[0][col]!=null)
				return true;
		}
		return false;
	}
	/*
	 * ����Ϸ��������
	 */
	public void paintGameOver(Graphics g) {
		if(isGameOver()) {
			tetromino=null;
			g.drawImage(gameover,-15,-15,null);
			Color color=new Color(0,71,157);
			g.setColor(color);
			Font font=new Font(Font.SERIF,Font.BOLD,30);
			g.setFont(font);
			g.drawString(""+score, 260, 207);
			g.drawString(""+lines, 260, 253);
			g.drawString(""+level, 260, 300);
			STATE=false;
				
		}
	}
	/*
	 * ����Ϸ��ͣ����
	 */
	public void paintGamePause(Graphics g){
		if (!STATE && !isGameOver()) {
			g.drawImage(pause, -15, -15, null);
		}
	}

	/*
	 * ����ʾ��Ϣ
	 */
	public void paintTabs(Graphics g) {
		
		//ȷ�����Ƶص�
		int x=410;
		int y=160;
		
		//������ɫ
		Color color=new Color(240,234,34);
		g.setColor(color);
		
		//��������
		Font f=new Font(Font.SERIF,Font.BOLD,30);
		g.setFont(f);
		g.drawString(""+score, x, y);
		y+=56;
		g.drawString(""+lines, x, y);
		y+=56;
		g.drawString(""+level, x, y);
		
	}
	/*
	 * ��Ԥ���������
	 */
	public void paintNextone(Graphics g) {
		if(nextone==null)
			return;
		Cell[] cells=nextone.cells;
		for(int i=0;i<cells.length;i++) {
			Cell c=cells[i];
			int row=c.getRow();
			int col=c.getCol()+9;
			int x=col*CELL_SIZE;
			int y=row*CELL_SIZE;
			g.drawImage(c.getBgImage(), x, y, null);
		}
	}
	/*
	 * ����ǰ�������
	 */
	public void paintTetromino(Graphics g) {
		//cells�������������䷽���ÿ�����ӵ�����
		if(tetromino==null)
			return;
		
		Cell[] cells=tetromino.cells;
		for(int i=0;i<cells.length;i++) {
			Cell c=cells[i];//c��������������ÿ�����ӵ�����
			int col=c.getCol();
			int row=c.getRow();
			int x=col*CELL_SIZE;
			int y=row*CELL_SIZE;
			g.drawImage(c.getBgImage(), x, y, null);
		}
	}
	/*
	 * ��ǽ
	 */
	public void paintWall(Graphics g) {
		for(int row = 0; row < ROWS; row++) {
			for(int col = 0; col < COLS; col++) {
				Cell cell = wall[row][col];
				int rows = row * CELL_SIZE;
				int cols = col * CELL_SIZE;
					
				if (cell == null) {
					//System.out.println(0);
				}else{
					g.drawImage(cell.getBgImage(), cols, rows, null);
				}
			}
		}
	}
	public static void startTetris() {
		JFrame frame = new JFrame();
			
		Tetris tetris = new Tetris();
			
		frame.add(tetris);
		frame.setSize(525, 600);
		frame.setLocationRelativeTo(null);
			
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
		//��ʾ����ʱ��ᾡ���������paint()����
		//������ʾ�����ݣ����Ʊ����ȣ�
		frame.setVisible(true);
			
		tetris.action();//����д��paint��������������ͼƬ
	}
	
}
