package cdb.exp.qc.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import cdb.common.lang.ExceptionUtil;
import cdb.common.model.RegionAnomalyInfoVO;

/**
 * 
 * @author Chao Chen
 * @version $Id: RegionImageJPanel.java, v 0.1 Nov 2, 2015 11:44:07 AM chench
 *          Exp $
 */
public class RegionImageJPanel extends JPanel {
	/** default values */
	private static final long serialVersionUID = 1L;
	/** image handler */
	private BufferedImage image;

	public RegionImageJPanel(String fileImg, List<RegionAnomalyInfoVO> regnAnVOArr) {
		updateImage(fileImg, regnAnVOArr);
	}

	/**
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(image, 0, 0, this);
	}

	public void updateImage(String fileImg, List<RegionAnomalyInfoVO> regnAnVOArr) {
		try {
			image = ImageIO.read(new FileInputStream(fileImg));
			Graphics2D graphc = image.createGraphics();

			int newSeq = 0;
			graphc.setColor(Color.WHITE);
			for (RegionAnomalyInfoVO regnAnVO : regnAnVOArr) {
				graphc.drawRect(regnAnVO.getY() - regnAnVO.getWidth(), regnAnVO.getX() + regnAnVO.getHeight(),
						regnAnVO.getWidth(), regnAnVO.getHeight());
				graphc.drawString("" + newSeq, regnAnVO.getY() - regnAnVO.getWidth(),
						regnAnVO.getX() + regnAnVO.getHeight());
				newSeq++;
			}

			// scroll panel
			JPanel canvas = new JPanel() {
				private static final long serialVersionUID = 1L;

				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.drawImage(image, 0, 0, null);
				}
			};
			canvas.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
			JScrollPane sp = new JScrollPane(canvas);
			setLayout(new BorderLayout());
			add(sp, BorderLayout.CENTER);
		} catch (IOException e) {
			ExceptionUtil.caught(e, "Image loading error: " + fileImg);
		}
	}
}
