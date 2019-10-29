package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.ScrollPane;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.*;
import javax.swing.text.DefaultCaret;

import Model.Graph;
import Model.Rater;
import Model.Review;
import Model.Reviewer;

/**
 * UserInterface is the class that controls all the input and output of the True Review Application
 * @author Jacoby & Sandeep
 *
 */
public class UserInterface
{
	//Variables necessary for graphics
	private static JFrame frame = new JFrame ("True Review: Loading");
	private static JPanel mainPane = new JPanel (), topBar = new JPanel (new BorderLayout()), centerPane, whoRevPane, prodPane, submitReviewPanel;
	private static JLabel output = new JLabel ();
	private static JProgressBar progBar = new JProgressBar();
	private static Graph g;
	private static JTextArea ta;
	private static JTextField prodField, whoRevField, helpfulText, nameText, summText;
	private static JLabel nameTitle, helpTitle, summTitle, revTitle;
	private static JButton submit;
	private static boolean [] panelSelected = new boolean [3];
	private static Color topBarC = new Color (35,47,63), submitC = new Color(255,153,0);
	private static ImageIcon logo = new ImageIcon ("Assets/logo.PNG");
	private static boolean preClicked;
	private static boolean [] clicked;
	private static JLabel spacer, spacer2, spacer3, spacer4;
	private static JScrollPane s;

	private static ButtonListener bs = new ButtonListener ();

	/**
	 * Pre-constructs all of the UserInterface then begins data processing
	 */
	public static void buildGUI ()
	{

		buildProgressBar();
		buildProdPanel();
		buildWhoRevPanel();
		buildSubmitReview();
		buildTopBar();
		panelSelected[0] =true;//Sets default tab to Product

		g = new Graph ();//Constructs dataset
		processDone();//Opens up user interface
	}
	/**
	 * Builds the loading bar
	 */
	private static void buildProgressBar() {
		
		frame.setSize(290, 95);//Makes new frame to store the progBar
		progBar.setValue(0);
		progBar.setForeground(Color.GREEN);
		progBar.setBorderPainted(false);
		progBar.setStringPainted(true);
		progBar.setString(0 + "%");
		//Sets color and text of ProgressBar
		UIManager.put("ProgressBar.selectionBackground", Color.BLACK);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		output.setText ("Processing Dataset: \n");
		output.setForeground(Color.WHITE);
		mainPane.setBackground(Color.DARK_GRAY);
		mainPane.add(output);
		mainPane.add(progBar);
		frame.add(mainPane);
		frame.setVisible(true);
		
	}
	/**
	 * Updates the value of progBar
	 * @param i The new percent of completion to be displayed in decimal form
	 */
	public static void updateProgressBar (double i)
	{
		progBar.setValue((int)(i*100));
		progBar.setString((int)(i*100)+ "%");
	}
	/**
	 * Builds top panel
	 */
	private static void buildTopBar()
	{
		JPanel topPane = new JPanel (new GridLayout (0,4));
		topPane.setBackground(topBarC);
		topPane.add(new JLabel (logo),BorderLayout.EAST);
	}
	/**
	 * Constructs main GUI and displays it on screen
	 */
	private static void processDone ()
	{
		frame.dispose(); // terminate the progress bar
		
		frame = new JFrame ("True Review: Industrial Edition");
		frame.setSize(700, 500);
		submit = new JButton ("Submit");
		submit.addActionListener(bs);
		mainPane.setBackground(Color.DARK_GRAY);
		mainPane = new JPanel (new BorderLayout());
		JPanel topPane = new JPanel (new GridLayout (0,4));
		topPane.setBackground(topBarC);
		topPane.add(new JLabel (logo));
		//New JButton for product
		JButton prod = new JButton ("Product");
		prod.addActionListener(bs);
		prod.setBackground(topBarC);
		prod.setForeground(Color.WHITE);
		topPane.add(prod);
		//Button for reviewer tab
		JButton reviewer = new JButton ("Reviewer");
		reviewer.addActionListener(bs);
		reviewer.setBackground(topBarC);
		reviewer.setForeground(Color.WHITE);
		topPane.add(reviewer);
		//Button for review tab
		JButton review = new JButton ("Review");
		review.addActionListener(bs);
		review.setBackground(topBarC);
		review.setForeground(Color.WHITE);
		topPane.add(review);
		//adds topPan
		mainPane.add(topPane, BorderLayout.NORTH);
		//New centerPaen
		centerPane = new JPanel (new GridBagLayout());
		centerPane.setBackground(new Color(255,255,255));
		//makes the default center pane the product tab
		centerPane.add(prodPane);
		mainPane.add(centerPane, BorderLayout.CENTER);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(mainPane);
		frame.setVisible(true);
	}
	/**
	 * Generate the product tab
	 */
	private static void buildProdPanel ()
	{
		preClicked = false;
		submit = new JButton ("Submit");
		submit.addActionListener(new ButtonListener());
		submit.setBackground(submitC);
		submit.setPreferredSize(new Dimension (90,30));
		//Setting for the prodPane
		prodPane = new JPanel (new FlowLayout(FlowLayout.CENTER));
		prodPane.setBackground(Color.WHITE);
		prodField = new JTextField ();
		prodField.setPreferredSize(new Dimension (230,30));
		prodField.setText ("Insert Product ID");

		prodField.setForeground(Color.GRAY);
		//Listener to remove grey text displayed inside text field when clicked
		prodField.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (!preClicked)
				{
					prodField.setText("");
					prodField.setForeground(Color.BLACK);
					preClicked = true;
				}
			}
		});

		prodPane.add(prodField);
		prodPane.add(submit);
	}
	/**
	 * Generates the panel for the reviewer tab
	 */
	private static void buildWhoRevPanel ()
	{
		preClicked = false;
		submit = new JButton ("Submit");
		submit.addActionListener(new ButtonListener());
		submit.setBackground(submitC);
		submit.setPreferredSize(new Dimension (90,30));
		//sets up panel settings and adds nescary componenets
		whoRevPane = new JPanel (new FlowLayout(FlowLayout.CENTER));
		whoRevPane.setBackground(Color.WHITE);
		whoRevField = new JTextField ();
		whoRevField.setPreferredSize(new Dimension (230,30));
		whoRevField.setText ("Insert Reviewer Name");
		whoRevField.setForeground(Color.GRAY);
		//Listener to remove grey text displayed inside text field when clicked
		whoRevField.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (!preClicked)
				{
					whoRevField.setText("");
					whoRevField.setForeground(Color.BLACK);
					preClicked = true;
				}
			}
		});


		whoRevPane.add(whoRevField);
		whoRevPane.add(submit);
	}
	/**
	 * Builds the submit Review panel
	 */
	private static void buildSubmitReview ()
	{
		preClicked = false;
		clicked = new boolean [3];
		submitReviewPanel = new JPanel();
		submitReviewPanel.setBackground(Color.WHITE);
		
		spacers(false); // using spaces for readability
		//Sets up static variable values
		nameTitle = new JLabel("UserID: ");
		nameText = new JTextField (10);
		nameText.setPreferredSize(new Dimension (200,30));
		nameText.setText ("What is your amazon ID?");

		helpTitle = new JLabel("Helpfulness:");
		helpfulText = new JTextField ();
		helpfulText.setPreferredSize(new Dimension (500,30));
		helpfulText.setText ("How helpful was your review?");

		summTitle = new JLabel("Summary:");
		summText = new JTextField ();
		summText.setPreferredSize(new Dimension (500,30));
		summText.setText ("How would you summarize your review?");

		revTitle = new JLabel("Review:");
		ta = new JTextArea(5,20);
		ta.setWrapStyleWord(true);
		ta.setText ("What did you think about the product?");

		submitReviewPanel.setLayout(new BoxLayout(submitReviewPanel, BoxLayout.Y_AXIS));
		JTextField[] textFields = new JTextField[3];
		ta.setForeground(Color.GRAY);

		ta.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e){//Listener to remove grey text displayed inside text field when clicked
				if (!preClicked)
				{
					ta.setText("");
					ta.setForeground(Color.BLACK);
					preClicked = true;
				}
			}
		});

		nameText.setForeground(Color.GRAY);
		nameText.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)//Listener to remove grey text displayed inside text field when clicked
			{	
				if (!clicked[0])
				{
					nameText.setText("");
					nameText.setForeground(Color.BLACK);
					clicked[0] = true;
				}
			}

		});
		helpfulText.setForeground(Color.GRAY);
		helpfulText.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)//Listener to remove grey text displayed inside text field when clicked
			{	
				if (!clicked[1])
				{
					helpfulText.setText("");
					helpfulText.setForeground(Color.BLACK);
					clicked[1] = true;
				}
			}

		});
		summText.setForeground(Color.GRAY);
		summText.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)//Listener to remove grey text displayed inside text field when clicked
			{	
				if (!clicked[2])
				{
					summText.setText("");
					summText.setForeground(Color.BLACK);
					clicked[2] = true;
				}
			}

		});


		s = new JScrollPane(ta,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);


		addElements(submitReviewPanel);
		submitReviewPanel.add(submit);

	}
	/**
	 * Changes tabs of the user interface
	 * @param i The integer representing the panel to swap to
	 */
	public static void changePane (int i)
	{
		for (int j = 0; j < panelSelected.length; j++)
		{
			panelSelected [j] = false;
		}
		
		if (i == 0)
		{
			buildProdPanel();
			paneFraming(prodPane, 0);//Reframes to product tab
		}
		if (i == 1)
		{
			buildWhoRevPanel();
			paneFraming(whoRevPane, 1);//Reframes to reviewertab

		}
		if (i == 2)
		{
			buildSubmitReview();
			paneFraming(submitReviewPanel, 2);//Reframes to review tab
		}
		frame.revalidate();
	}
	/**
	 * Submits the input and generates output frame
	 */
	public static void submit ()
	{
		if (panelSelected[0])//if in product
		{
			//Generates an output panel to display the top three reviews for the product
			Review[] a = g.searchByProductId(prodField.getText());
			JFrame res = new JFrame ("True Review: Results");
			res.setSize(700, 500);
			JPanel p = new JPanel ();
			System.out.println (a.length);
			
			Arrays.sort(a);
			
			JScrollPane scr = new JScrollPane(p,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			for (int i = 0; i < a.length && i < 3;i++)
			{
			

				spacers(true); // using spaces for readability
				
				nameTitle = new JLabel("User ID: ");
				nameText = new JTextField (10);
				nameText.setPreferredSize(new Dimension (200,30));
				nameText.setText (a[a.length-1-i].userId);

				helpTitle = new JLabel("Helpfulness:");
				helpfulText = new JTextField ();
				helpfulText.setPreferredSize(new Dimension (200,30));
				
				String help;
				if (a[a.length-1-i].helpfulness == (-1))
				{
					help = "No Rating";
				}
				else help = "" + a[a.length-1-i].helpfulness;
				helpfulText.setText (help);

				summTitle = new JLabel("Summary:");
				summText = new JTextField ();
				summText.setPreferredSize(new Dimension (200,30));
				summText.setText (a[a.length-1-i].summary);

				revTitle = new JLabel("Review:");
				ta = new JTextArea(5,20);
				ta.setText (a[a.length-1-i].text);

				s = new JScrollPane(ta,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

				p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

				addElements(p);
			}
			JPanel test = new JPanel (new BorderLayout());
			test.setBackground(Color.WHITE);
			test.add(scr, BorderLayout.CENTER);
			test.add(topBar, BorderLayout.NORTH);
			res.add(test);
			res.setVisible(true);
		}
		if (panelSelected[1])//if in reviewer tab
		{
			//Generates an output panel to display the top three reviews for the product
			Reviewer b = g.searchForUsername(whoRevField.getText());
			JFrame res = new JFrame ("True Review: Results");
			res.setSize(700, 500);
			JPanel p = new JPanel ();
			p.setBackground(Color.WHITE);
			ArrayList<Review> a = b.reviews;
			
			a.sort(null);
			JScrollPane scr = new JScrollPane(p,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			for (int i = 0; i < a.size() && i < 3;i++)
			{
				
				spacers(true); // using spaces for readability
				
				nameTitle = new JLabel("UserID: ");
				nameText = new JTextField (10);
				nameText.setPreferredSize(new Dimension (200,30));
				nameText.setText (a.get(a.size()-1-i).userId);

				helpTitle = new JLabel("Helpfulness:");
				helpfulText = new JTextField ();
				helpfulText.setPreferredSize(new Dimension (200,30));
				String help;
				if (a.get(a.size()-1-i).helpfulness == (-1))
				{
					help = "No Rating";
				}
				else help = "" + a.get(a.size()-1-i).helpfulness;
				helpfulText.setText (help);

				summTitle = new JLabel("Summary:");
				summText = new JTextField ();
				summText.setPreferredSize(new Dimension (200,30));
				summText.setText (a.get(a.size()-1-i).summary);

				revTitle = new JLabel("Review:");
				ta = new JTextArea(5,20);
				ta.setText (a.get(a.size()-1-i).text);

				s = new JScrollPane(ta,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

				p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

				addElements(p);
			}
			JPanel test = new JPanel (new BorderLayout());
			test.setBackground(Color.WHITE);
			test.add(scr, BorderLayout.CENTER);
			test.add(topBar, BorderLayout.NORTH);
			res.add(test);
			res.setVisible(true);
		}
		if (panelSelected[2])//If in review tab
		{
			double help;
			try
			{
				help = Double.parseDouble(helpfulText.getText());
			}
			catch (Exception r)
			{
				help = Rater.NO_VALUE_DOUBLE;
			}
			//Determines null values for submision to Rater
			String userID = nameText.getText();
			if (userID.equals("What is your amazon ID?")) userID = null;
			String summary = summText.getText();
			if (summary.equals("How would you summarize your review?")) summary = null;
			String reviewText = ta.getText();
			if (reviewText.equals("What did you think about the product?")) reviewText = null;
			
			//Generates a rating for the review
			Rater r = new Rater  (g,userID,help,summary,reviewText);
			//Makes a new frame to display results
			JFrame res = new JFrame ("True Review: Results");
			res.setSize(250, 70);
			JPanel p = new JPanel ();
			p.setBackground(Color.WHITE);
			JLabel t = new JLabel("Review Strength: " + r.getRating());

			p.add(t);
			res.add(p);
			res.setVisible(true);
			changePane(2);
		}
	}
	/**
	 * Instantiates spacers
	 * @param longSpace determines if space 4 should be a line or empty
	 */
	private static void spacers(boolean longSpace) {
		spacer = new JLabel("          ");
		spacer2 = new JLabel("          ");
		spacer3 = new JLabel("          ");
		if (longSpace) {
			spacer4 = new JLabel("_________________________________________________________________________________________________");
		} else {
			spacer4 = new JLabel("          ");
		}

	}
	/**
	 * Adds elements to panel
	 * @param panel the panel to have the elements added to
	 */
	private static void addElements(JPanel panel) {
		panel.add(nameTitle);
		panel.add(nameText);
		panel.add(spacer);
		panel.add(helpTitle);
		panel.add(helpfulText);
		panel.add(spacer2);
		panel.add(summTitle);
		panel.add(summText);
		panel.add(spacer3);
		panel.add(revTitle);
		panel.add(s);
		panel.add(spacer4);
	}
	/**
	 * Reframes centerPane and replaces with new panel
	 * @param panel the panel to add
	 * @param paneSelection the integer value representing the tab
	 */
	private static void paneFraming(JPanel panel, int paneSelection) {
		mainPane.remove(centerPane);
		centerPane = new JPanel (new GridBagLayout());
		centerPane.setBackground(new Color(255,255,255));
		centerPane.add(panel);
		mainPane.add(centerPane, BorderLayout.CENTER);
		panelSelected[paneSelection] = true;
	}
	
}
