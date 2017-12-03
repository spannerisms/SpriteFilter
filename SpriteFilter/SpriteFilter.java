package SpriteFilter;

import java.awt.Container;
import java.awt.Dimension;
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
import javax.swing.filechooser.FileNameExtensionFilter;

import SpriteManipulator.*;

import static javax.swing.SpringLayout.*;

public class SpriteFilter {
	// version number
	static final String VERSION = "v1.5";

	// class constants
	static final int SPRITESIZE = SpriteManipulator.SPRITE_DATA_SIZE; // invariable lengths
	static final int PALETTESIZE = SpriteManipulator.PAL_DATA_SIZE;
	static final String HEX = "0123456789ABCDEF"; // HEX values

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
		// try to set LaF
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e2) {
				// do nothing
		} //end System

		final JFrame frame = new JFrame("Sprite Filter " + VERSION); // frame name
		final Dimension d = new Dimension(600, 382);
		final Dimension d2 = new Dimension(300, 250);

		// about frame
		final JDialog aboutFrame = new JDialog(frame, "About");
		final TextArea aboutTextArea = new TextArea("", 0, 0, TextArea.SCROLLBARS_VERTICAL_ONLY);
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
		frame.setJMenuBar(menu);

		// file menu
		final JMenu fileMenu = new JMenu("File");
		menu.add(fileMenu);

		// exit
		final JMenuItem exit = new JMenuItem("Exit");
		ImageIcon mirror = new ImageIcon(
				SpriteFilter.class.getResource("/SpriteFilter/images/mirror.png")
			);
		exit.setIcon(mirror);
		exit.addActionListener(arg0 -> System.exit(0));
		fileMenu.add(exit);

		// end file menu

		// help menu
		final JMenu helpMenu = new JMenu("Help");
		menu.add(helpMenu);

		// Acknowledgements
		final JMenuItem peeps = new JMenuItem("About");
		ImageIcon mapIcon = new ImageIcon(
				SpriteFilter.class.getResource("/SpriteFilter/images/map.png")
			);
		peeps.setIcon(mapIcon);

		peeps.addActionListener(
				arg0 -> {
					aboutFrame.setVisible(true);
				});
		helpMenu.add(peeps);

		// end help menu

		// GUI layout
		Container wrap = frame.getContentPane();
		SpringLayout l = new SpringLayout();
		wrap.setLayout(l);

		// file name
		final JTextField fileName = new JTextField("");
		final JButton fileNameBtn = new JButton("Load sprite");

		l.putConstraint(NORTH, fileName, 5, NORTH, wrap);
		l.putConstraint(WEST, fileName, 5, WEST, wrap);	
		l.putConstraint(EAST, fileName, -5, WEST, fileNameBtn);
		wrap.add(fileName);

		l.putConstraint(NORTH, fileNameBtn, -2, NORTH, fileName);
		l.putConstraint(EAST, fileNameBtn, -5, EAST, wrap);
		wrap.add(fileNameBtn);

		// flags 
		final JLabel flagsLbl = new JLabel("Flag and filter");
		final JTextField flags = new JTextField();
		final JComboBox<Filter> options = new JComboBox<Filter>(Filter.values());

		l.putConstraint(NORTH, flagsLbl, 5, SOUTH, fileName);
		l.putConstraint(WEST, flagsLbl, 5, WEST, wrap);
		wrap.add(flagsLbl);

		l.putConstraint(NORTH, options, -2, NORTH, flagsLbl);
		l.putConstraint(WEST, options, 5, EAST, flagsLbl);
		wrap.add(options);

		l.putConstraint(NORTH, flags, 0, NORTH, options);
		l.putConstraint(WEST, flags, 5, EAST, options);
		l.putConstraint(EAST, flags, 5, HORIZONTAL_CENTER, wrap);
		wrap.add(flags);

		// apply button
		final JButton goBtn = new JButton("Apply filter!");
		l.putConstraint(NORTH, goBtn, -2, NORTH, options);
		l.putConstraint(WEST, goBtn, 5, EAST, flags);
		l.putConstraint(EAST, goBtn, -5, EAST, wrap);
		wrap.add(goBtn);

		// filter info
		final JTextPane flagTextInfo = new JTextPane();
		flagTextInfo.setEditable(false);
		flagTextInfo.setHighlighter(null);
		flagTextInfo.setBackground(null);
		l.putConstraint(NORTH, flagTextInfo, 5, SOUTH, flagsLbl);
		l.putConstraint(WEST, flagTextInfo, 5, WEST, wrap);
		l.putConstraint(EAST, flagTextInfo, -5, EAST, wrap);
		wrap.add(flagTextInfo);

		// ico
		final ImageIcon ico = new ImageIcon(
				SpriteFilter.class.getResource("/SpriteFilter/images/ico.png")
			);
		frame.setIconImage(ico.getImage());

		// frame display
		frame.setSize(d);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocation(200,200);

		// file explorer
		final BetterJFileChooser explorer = new BetterJFileChooser();
		explorer.setAcceptAllFileFilterUsed(false);
		explorer.setFileFilter(new FileNameExtensionFilter(
				"ALttP Sprite files", new String[] { ZSPRFile.EXTENSION }));
		explorer.setCurrentDirectory(new File(".")); // quick way to set to current .jar loc

		// can't clear text due to wonky code
		// have to set a blank file instead
		final File EEE = new File("");

		options.addActionListener(
			arg0 -> {
				Filter option = (Filter) options.getSelectedItem();
				String filterText = option.desc;
				String flagText = option.flagHelp;
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
					if (SpriteManipulator.testFileType(n, ZSPRFile.EXTENSION)) {
						fileName.setText(n);
					}
				}
			});

		goBtn.addActionListener(
			arg0 -> {
				String fileN = fileName.getText();
				ZSPRFile spr;
				try {
					spr = ZSPRFile.readFile(fileN);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(frame,
							"Error reading sprite",
							"Oops",
							JOptionPane.WARNING_MESSAGE);
					return;
				} catch (ZSPRFormatException e) {
					JOptionPane.showMessageDialog(frame,
							e.getMessage(),
							"PROBLEM",
							JOptionPane.WARNING_MESSAGE);
					return;
				}

				Filter option = (Filter) options.getSelectedItem();
				String optionName = option.toString().toLowerCase();
				byte[] sprData = spr.getSpriteData();
				byte[][][] eightXeight = SpriteManipulator.makeSpr8x8(sprData);
				eightXeight = option.runFilter(eightXeight, flags.getText());
				byte[] fullMap = SpriteManipulator.export8x8ToSPR(eightXeight);
				spr.setSpriteData(fullMap);

				String exportedName = fileN.substring(0,fileN.lastIndexOf('.')) +
						" (" + optionName + ")." + ZSPRFile.EXTENSION;
				String sName = spr.getSpriteName() + " (" + optionName + ")";
				spr.setSpriteName(sName);

				try {
					SpriteManipulator.writeSPRFile(exportedName, spr);
				} catch (IOException
						| ZSPRFormatException e) {
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
	 * Randomizes all desired pixels
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

	/**
	 * List of filters with descriptions and lambda'd operation
	 */
	private enum Filter {
		STATIC ("Static",
				"Randomizes pixels of specific indices.",
				"Flag accepts HEX values (0-F) of which indices to randomize; defaults to 1-F.\n" +
						"Prefix with '-' to inverse selection.",
						(ebe, flags) -> { return staticFilter(ebe, flags); }
				),
		INDEX_SWAP ("Index swap",
					"Swaps pixel indices to the other end of the palette, ignoring transparent colors; "
							+ "e.g. 0x1 with 0xF, 0x2 with 0xE, etc.",
							(ebe, flags) -> { return swapFilter(ebe); }
				),
		LINE_SHIFT ("Line shift",
					"Shifts even rows to the right and odd rows to the left by 1 pixel.",
					(ebe, flags) -> { return lineShiftFilter(ebe); }
				),
		PALETTE_SHIFT ("Palette shift",
						"Shifts all pixels a specific number of palette indices to the right.",
						"Flag accepts an integer number (decimal) of spaces to shift each index; defaults to 5",
						(ebe, flags) -> { return palShiftFilter(ebe, flags); }
				),
		ROW_SWAP ("Row swap",
					"Swaps even rows with odd rows.",
					(ebe, flags) -> { return rowSwapFilter(ebe); }
				),
		COLUMN_SWAP ("Column swap",
					"Swaps even columns with odd columns.",
					(ebe, flags) -> { return columnSwapFilter(ebe); }
				),
		BUZZ_SWAP ("Buzz swap",
					"Swaps both even and odd rows and columns, simultaneously.",
					(ebe, flags) -> { return buzzSwapFilter(ebe); }
				),
		X_SQUISH ("X-Squish",
					"Squishes sprite horizontally.",
					(ebe, flags) -> { return squishFilter(ebe); }
				),
		Y_SQUISH ("Y-Squish",
					"Squishes sprite vertically.",
					(ebe, flags) -> { return squashFilter(ebe); }
				);

		// local vars
		public final String name;
		public final String desc;
		public final String flagHelp;
		private final FilterOp runner;

		private Filter(String name, String desc, String flagHelp, FilterOp runner) {
			this.name = name;
			this.desc = desc;
			this.flagHelp = flagHelp;
			this.runner = runner;
		}

		private Filter(String name, String desc, FilterOp runner) {
			this(name, desc, "No flag options available for this filter.", runner);
		}

		public String toString() {
			return name;
		}

		public byte[][][] runFilter(byte[][][] ebe, String flags) {
			return runner.doIt(ebe, flags);
		}

		private interface FilterOp {
			byte[][][] doIt(byte[][][] ebe, String flags);
		}
	}
}