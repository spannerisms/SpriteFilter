package SpriteFilter;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;

import SpriteManipulator.*;

public class SpriteFilter {
	// version number
	static final String VERSION = "v1.4";

	// class constants
	static final int SPRITESIZE = SpriteManipulator.SPRITE_DATA_SIZE; // invariable lengths
	static final int PALETTESIZE = SpriteManipulator.PAL_DATA_SIZE;
	static final String HEX = "0123456789ABCDEF"; // HEX values

	// format of snes 4bpp {row (r), bit plane (b)}
	// bit plane 0 indexed such that 1011 corresponds to 0123
	static final int BPPI[][] = {
			{0,0},{0,1},{1,0},{1,1},{2,0},{2,1},{3,0},{3,1},
			{4,0},{4,1},{5,0},{5,1},{6,0},{6,1},{7,0},{7,1},
			{0,2},{0,3},{1,2},{1,3},{2,2},{2,3},{3,2},{3,3},
			{4,2},{4,3},{5,2},{5,3},{6,2},{6,3},{7,2},{7,3}
	};

	// descriptions of filters
	static final String[][] FILTERS = {
			{ "Static",
				"Randomizes pixels of specific indices.",
				"Flag accepts HEX values (0-F) of which indices to randomize; defaults to 1-F.\n" +
				"Prefix with '-' to inverse selection."},
			{ "Index swap",
				"Swaps pixel indices to the other end of the palette, ignoring transparent colors"
					+ "; e.g. 0x1 with 0xF, 0x2 with 0xE, etc.",
				null },
			{ "Line shift",
				"Shifts even rows to the right and odd rows to the left by 1 pixel.",
				null },
			{ "Palette shift",
				"Shifts all pixels a specific number of palette indices to the right.",
				"Flag accepts an integer number (decimal) of spaces to shift each index; defaults to 5"},
			{ "Row swap",
				"Swaps even rows with odd rows.",
				null },
			{ "Column swap",
				"Swaps even columns with odd columns.",
				null },
			{ "Buzz swap",
				"Swaps both even and odd rows and columns, simultaneously.",
				null },
			{ "X-Squish",
				"Squishes sprite horizontally.",
				null },
			{ "Y-Squish",
				"Squishes sprite vertically.",
				null },
			};

	// main
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				doTheGUI();
			}
		});
	}

	// GUI
	public static void doTheGUI() {
		// have to have this up here or LAF overrides everything
		// stupid LAF
		final JTextPane flagTextInfo = new JTextPane();
		flagTextInfo.setFont(new Font(flagTextInfo.getFont().getFontName(), Font.PLAIN, 10));
		flagTextInfo.setEditable(false);
		flagTextInfo.setHighlighter(null);
		flagTextInfo.setBackground(null);

		//try to set LAF
		try {
			UIManager.setLookAndFeel("metal");
		} catch (UnsupportedLookAndFeelException
				| ClassNotFoundException
				| InstantiationException
				| IllegalAccessException e) {
			// try to set System default
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (UnsupportedLookAndFeelException
					| ClassNotFoundException
					| InstantiationException
					| IllegalAccessException e2) {
					// do nothing
			} //end System
		} // end LAF

		final JFrame frame = new JFrame("Sprite Filter " + VERSION); // frame name
		final Dimension d = new Dimension(600, 382);
		final Dimension d2 = new Dimension(300, 250);

		// about frame
		final JDialog aboutFrame = new JDialog(frame, "About");
		final TextArea aboutTextArea = new TextArea("",0,0,TextArea.SCROLLBARS_VERTICAL_ONLY);
		aboutTextArea.setEditable(false);
		aboutTextArea.append("Written by fatmanspanda"); // hey, that's me
		aboutTextArea.append("\n\nSpecial thanks:\nMikeTrethewey"); // force me to do this and falls in every category
		aboutTextArea.append("\n\nResources and development:\n");
		aboutTextArea.append(String.join(", ",
				new String[]{
						"Veetorp", // provided most valuable documentation
						"Zarby89", // various documentation and answers
						"Sosuke3" // various snes code answers
				}));
		aboutTextArea.append("\n\nUpdates at:\n");
		aboutTextArea.append(String.join(", ",
				new String[]{
						"http://github.com/fatmanspanda/ALttPNG/wiki"
				}));
		aboutFrame.add(aboutTextArea);
		aboutFrame.setSize(d2);

		// menu
		final JMenuBar menu = new JMenuBar();
		final JMenu fileMenu = new JMenu("File");

		// exit
		final JMenuItem exit = new JMenuItem("Exit");
		ImageIcon mirror = new ImageIcon(
				SpriteFilter.class.getResource("images/mirror.png")
			);
		exit.setIcon(mirror);
		fileMenu.add(exit);
		exit.addActionListener(arg0 -> System.exit(0));

		menu.add(fileMenu);
		// end file menu

		// help menu
		final JMenu helpMenu = new JMenu("Help");

		// Acknowledgements
		final JMenuItem peeps = new JMenuItem("About");
		ImageIcon mapIcon = new ImageIcon(
				SpriteFilter.class.getResource("images/map.png")
			);
		peeps.setIcon(mapIcon);
		helpMenu.add(peeps);
		peeps.addActionListener(
				arg0 -> {
					aboutFrame.setVisible(true);
				});
		menu.add(helpMenu);
		// end help menu

		frame.setJMenuBar(menu);

		// filters
		String[] filterNames = new String[FILTERS.length];
		for (int i = 0; i < filterNames.length; i++) {
			filterNames[i] = FILTERS[i][0];
		}

		// GUI layout
		Container wrap = frame.getContentPane();
		SpringLayout l = new SpringLayout();
		wrap.setLayout(l);

		// file name
		final JTextField fileName = new JTextField("");
		final JButton fileNameBtn = new JButton("Load sprite");

		l.putConstraint(SpringLayout.NORTH, fileName, 5,
				SpringLayout.NORTH, wrap);
		l.putConstraint(SpringLayout.WEST, fileName, 5,
				SpringLayout.WEST, wrap);	
		l.putConstraint(SpringLayout.EAST, fileName, -5,
				SpringLayout.WEST, fileNameBtn);
		wrap.add(fileName);

		l.putConstraint(SpringLayout.NORTH, fileNameBtn, -2,
				SpringLayout.NORTH, fileName);
		l.putConstraint(SpringLayout.EAST, fileNameBtn, -5,
						SpringLayout.EAST, wrap);
		wrap.add(fileNameBtn);

		// flags 
		final JLabel flagsLbl = new JLabel("Flag and filter");
		final JTextField flags = new JTextField();
		final JComboBox<String> options = new JComboBox<String>(filterNames);

		l.putConstraint(SpringLayout.NORTH, flagsLbl, 5,
				SpringLayout.SOUTH, fileName);
		l.putConstraint(SpringLayout.WEST, flagsLbl, 5,
						SpringLayout.WEST, wrap);
		wrap.add(flagsLbl);

		l.putConstraint(SpringLayout.NORTH, options, -2,
				SpringLayout.NORTH, flagsLbl);
		l.putConstraint(SpringLayout.WEST, options, 5,
				SpringLayout.EAST, flagsLbl);
		wrap.add(options);

		l.putConstraint(SpringLayout.NORTH, flags, 0,
				SpringLayout.NORTH, options);
		l.putConstraint(SpringLayout.WEST, flags, 5,
				SpringLayout.EAST, options);
		l.putConstraint(SpringLayout.EAST, flags, 5,
				SpringLayout.HORIZONTAL_CENTER, wrap);
		wrap.add(flags);

		// apply button
		final JButton goBtn = new JButton("Apply filter!");
		l.putConstraint(SpringLayout.NORTH, goBtn, -2,
				SpringLayout.NORTH, options);
		l.putConstraint(SpringLayout.WEST, goBtn, 5,
				SpringLayout.EAST, flags);
		l.putConstraint(SpringLayout.EAST, goBtn, -5,
				SpringLayout.EAST, wrap);
		wrap.add(goBtn);

		// filter info
		l.putConstraint(SpringLayout.NORTH, flagTextInfo, 5,
				SpringLayout.SOUTH, flagsLbl);
		l.putConstraint(SpringLayout.WEST, flagTextInfo, 5,
				SpringLayout.WEST, wrap);
		l.putConstraint(SpringLayout.EAST, flagTextInfo, -5,
				SpringLayout.EAST, wrap);
		wrap.add(flagTextInfo);

		// ico - Credit goes to Hoodyha
		final ImageIcon ico = new ImageIcon(
				SpriteFilter.class.getResource("images/ico.png")
			);
		frame.setIconImage(ico.getImage());

		// frame display
		frame.setSize(d);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocation(200,200);

		// file explorer
		final JFileChooser explorer = new JFileChooser();
		explorer.setAcceptAllFileFilterUsed(false);
		explorer.setFileFilter(new FileNameExtensionFilter(
				"ALttP Sprite files", new String[] { SPRFile.EXTENSION }));
		explorer.setCurrentDirectory(new File(".")); // quick way to set to current .jar loc

		// can't clear text due to wonky code
		// have to set a blank file instead
		final File EEE = new File("");

		options.addActionListener(
			arg0 -> {
				int option = options.getSelectedIndex();
				String filterText = FILTERS[option][1];
				String flagText = FILTERS[option][2];
				if (flagText == null) {
					flagText = "No flag options available for this filter.";
				}
				flagTextInfo.setText(filterText + "\n" + flagText);
			});

		fileNameBtn.addActionListener(
			arg0 -> {
				explorer.setSelectedFile(EEE);
				int option = explorer.showOpenDialog(fileNameBtn);
				if (option == JFileChooser.CANCEL_OPTION) {
					return;
				}
				String n = "";
				try {
					n = explorer.getSelectedFile().getPath();
				} catch (NullPointerException e) {
					// do nothing
				} finally {
					if (SpriteManipulator.testFileType(n,SPRFile.EXTENSION)) {
						fileName.setText(n);
					}
				}
			});

		goBtn.addActionListener(
			arg0 -> {
				String fileN = fileName.getText();
				SPRFile spr;
				try {
					spr = SPRFile.readFile(fileN);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(frame,
							"Error reading sprite",
							"Oops",
							JOptionPane.WARNING_MESSAGE);
					return;
				} catch (ObsoleteSPRFormatException e) {
					JOptionPane.showMessageDialog(frame,
							e.getMessage(),
							"Y'all old",
							JOptionPane.WARNING_MESSAGE);
					return;
				} catch (NotZSPRException e) {
					JOptionPane.showMessageDialog(frame,
							"File is not a " + SPRFile.EXTENSION + " file",
							"Not my job",
							JOptionPane.WARNING_MESSAGE);
					return;
				} catch (BadChecksumException e) {
					JOptionPane.showMessageDialog(frame,
							"Bad Checksum; file may be corrupted",
							"Invalid",
							JOptionPane.WARNING_MESSAGE);
					return;
				}

				int filterToken = options.getSelectedIndex();
				byte[] sprData = spr.getSpriteData();
				byte[][][] eightXeight = SpriteManipulator.makeSpr8x8(sprData);
				eightXeight = filter(eightXeight,filterToken, flags.getText());
				byte[] fullMap = SpriteManipulator.export8x8ToSPR(eightXeight);
				spr.setSpriteData(fullMap);

				String exportedName = fileN.substring(0,fileN.lastIndexOf('.')) +
						" (" + FILTERS[filterToken][0].toLowerCase() + ")." + SPRFile.EXTENSION;
				String sName = spr.getSpriteName() + " (" + FILTERS[filterToken][0].toLowerCase() + ")";
				spr.setSpriteName(sName);

				try {
					SpriteManipulator.writeSPRFile(exportedName, spr);
				} catch (IOException
						| NotZSPRException
						| BadChecksumException
						e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(frame,
							"Error writing sprite",
							"Oops",
							JOptionPane.WARNING_MESSAGE);
					return;
				}
				String shortName = exportedName.substring(exportedName.lastIndexOf('\\')+1);
				JOptionPane.showMessageDialog(frame,
						"Sprite successfully filtered and written to:\n" + shortName,
						"YAY",
						JOptionPane.PLAIN_MESSAGE);
			});

		// random crap to fire an event to update text
		options.getActionListeners()[0].actionPerformed(
				new ActionEvent(options, ActionEvent.ACTION_PERFORMED,"",0,0));
		frame.setVisible(true);
	}

	/**
	 * Apply a filter based on a token.
	 * @param img - image map to screw up
	 * @param c - filter token
	 * @param f - flag
	 */
	public static byte[][][] filter(byte[][][] img, int c, String f) {
		byte[][][] ret = img.clone();
		switch(c) {
			case 0 :
				ret = staticFilter(ret, f);
				break;
			case 1 :
				ret = swapFilter(ret);
				break;
			case 2 :
				ret = lineShiftFilter(ret);
				break;
			case 3 :
				ret = palShiftFilter(ret, f);
				break;
			case 4 :
				ret = rowSwapFilter(ret);
				break;
			case 5 :
				ret = columnSwapFilter(ret);
				break;
			case 6 :
				ret = buzzSwapFilter(ret);
				break;
			case 7 :
				ret = squishFilter(ret);
				break;
			case 8 :
				ret = squashFilter(ret);
				break;
		}

		return ret;
	}

	/**
	 * Randomizes all desired pixels.
	 * @param img
	 */
	public static byte[][][] staticFilter(byte[][][] img, String f) {
		// default
		if (f.equals("")) {
			f = "-0";
		}
		// check if we're inversed
		boolean inversed = false;
		try {
			inversed = f.charAt(0) == '-';
		} catch (Exception e) {
			// do nothing
		}
		// clear all non HEX values
		f = f.toUpperCase();
		f = f.replaceAll("[^0-9A-F]", "");

		// default to all but trans pixel
		// find which hex numbers exist in flags
		boolean[] randomize = new boolean[16];
		for (int i = 0; i < HEX.length(); i++) {
			if (f.indexOf(HEX.charAt(i)) != -1) {
				randomize[i] = true;
			}
		}
		if (inversed) {
			for (int i = 0; i < randomize.length; i++) {
				randomize[i] = !randomize[i];
			}
		}

		for (int i = 0; i < img.length; i++) {
			for (int j = 0; j < img[0].length; j++) {
				for (int k = 0; k < img[0][0].length; k++) {
					if (randomize[img[i][j][k]] == true) {
						img[i][j][k] = (byte) (Math.random() * 16);
					}
				}
			}
		}
		return img;
	}

	/**
	 * Swaps indices with the other end; e.g. 0x1 swapped with 0xF, 0x2 swapped with 0xE, etc.
	 * Ignores trans pixels
	 */
	public static byte[][][] swapFilter(byte[][][] img) {
		for (int i = 0; i < img.length; i++) {
			for (int j = 0; j < img[0].length; j++) {
				for (int k = 0; k < img[0][0].length; k++) {
					if (img[i][j][k] != 0) {
						img[i][j][k] = (byte) (16 - img[i][j][k]);
					}
				}
			}
		}
		return img;
	}

	/**
	 * Shifts rows by 1 to the left or right, alternating
	 */
	public static byte[][][] lineShiftFilter(byte[][][] img) {
		for (int i = 0; i < img.length; i++) {
			for (int j = 0; j < img[0].length; j++) {
				for (int k = 0; k < img[0][0].length; k++) {
					if (j % 2 == 0) {
						if (k != 7) {
							img[i][j][7-k] = img[i][j][6-k];
						}
						else {
							img[i][j][7-k] = 0;
						}
					} else {
						if (k != 7) {
							img[i][j][k] = img[i][j][k+1];
						}
						else {
							img[i][j][k] = 0;
						}
					}
				}
			}
		}
		return img;
	}

	/**
	 * Shifts all non transparent indices an integer value to the right, wrapping around;
	 * defaults to 5.
	 * @param img
	 * @param f
	 */
	public static byte[][][] palShiftFilter(byte[][][] img, String f) {
		int wrap = 5;
		try {
			wrap = Integer.parseInt(f, 10);
		} catch (NumberFormatException e) {
			// do nothing
		}
		for (int i = 0; i < img.length; i++) {
			for (int j = 0; j < img[0].length; j++) {
				for (int k = 0; k < img[0][0].length; k++) {
					if (img[i][j][k] != 0) {
						img[i][j][k] = (byte) ((img[i][j][k] + wrap) % 16);
					}
				}
			}
		}
		return img;
	}

	/**
	 * Swap even and odd rows
	 * @param img
	 */
	public static byte[][][] rowSwapFilter(byte[][][] img) {
		byte[][] copy = new byte[8][8];
		for (int i = 0; i < img.length; i++) {
			// copy array, .clone() is stupid
			for (int i2 = 0; i2 < copy.length; i2++) {
				for (int j2 = 0; j2 < copy.length; j2++) {
					copy[i2][j2] = img[i][i2][j2];
				}
			}

			for (int j = 0; j < img[0].length; j++) {
				int dir = (j%2) == 0 ? 1 : -1;
				for (int k = 0; k < img[0][0].length; k++) {
					img[i][j][k] = copy[j+dir][k];
				}
			}
		}
		return img;
	}

	/**
	 * Swap even and odd columns
	 * @param img
	 */
	public static byte[][][] columnSwapFilter(byte[][][] img) {
		byte[][] copy = new byte[8][8];
		for (int i = 0; i < img.length; i++) {
			// copy array, .clone() is stupid
			for (int i2 = 0; i2 < copy.length; i2++) {
				for (int j2 = 0; j2 < copy.length; j2++) {
					copy[i2][j2] = img[i][i2][j2];
				}
			}

			for (int j = 0; j < img[0].length; j++) {
				for (int k = 0; k < img[0][0].length; k++) {
					int dir = (k%2) == 0 ? 1 : -1;
					img[i][j][k] = copy[j][k+dir];
				}
			}
		}
		return img;
	}

	/**
	 * Swap even and odd rows and columns
	 * @param img
	 */
	public static byte[][][] buzzSwapFilter(byte[][][] img) {
		byte[][] copy = new byte[8][8];
		for (int i = 0; i < img.length; i++) {
			// copy array, .clone() is stupid
			for (int i2 = 0; i2 < copy.length; i2++) {
				for (int j2 = 0; j2 < copy.length; j2++) {
					copy[i2][j2] = img[i][i2][j2];
				}
			}

			for (int j = 0; j < img[0].length; j++) {
				int dir =  (j%2) == 0 ? 1 : -1;
				for (int k = 0; k < img[0][0].length; k++) {
					int dir2 = (k%2) == 0 ? 1 : -1;
					img[i][j][k] = copy[j+dir][k+dir2];
				}
			}
		}
		return img;
	}

	/**
	 * Squish horizontally
	 * @param img
	 */
	public static byte[][][] squishFilter(byte[][][] img) {
		for (int i = 0; i < img.length; i++) {
			for (int j = 0; j < img[0].length; j++) {
				for (int k = 0; k < img[0][0].length; k++) {
					int dir = (k%2) == 0 ? 1 : -1;
					img[i][j][k] = img[i][j][k+dir];
				}
			}
		}
		return img;
	}

	/**
	 * Squish vertically
	 * @param img
	 */
	public static byte[][][] squashFilter(byte[][][] img) {
		for (int i = 0; i < img.length; i++) {
			for (int j = 0; j < img[0].length; j++) {
				int dir = (j%2) == 0 ? 1 : -1;
				for (int k = 0; k < img[0][0].length; k++) {
					img[i][j][k] = img[i][j+dir][k];
				}
			}
		}
		return img;
	}
}