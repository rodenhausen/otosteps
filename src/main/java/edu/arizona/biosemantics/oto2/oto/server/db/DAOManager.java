package edu.arizona.biosemantics.oto2.oto.server.db;

public class DAOManager {

	private BucketDAO bucketDAO;
	private CollectionDAO collectionDAO;
	private TermDAO termDAO;
	private LabelDAO labelDAO;
	private LabelingDAO labelingDAO;
	private ContextDAO contextDAO;
	private SynonymDAO synonymDAO;
	private OntologyDAO ontologyDAO;
	private CommentDAO commentDAO;
	private CommunityDAO communityDAO;
	
	public DAOManager() {
		bucketDAO = new BucketDAO();
		collectionDAO = new CollectionDAO();
		termDAO = new TermDAO();
		labelDAO = new LabelDAO();
		labelingDAO = new LabelingDAO();
		contextDAO = new ContextDAO();
		synonymDAO = new SynonymDAO();
		ontologyDAO = new OntologyDAO();
		commentDAO = new CommentDAO();
		communityDAO = new CommunityDAO();
		
		bucketDAO.setLabelingDAO(labelingDAO);
		bucketDAO.setTermDAO(termDAO);
		collectionDAO.setBucketDAO(bucketDAO);
		collectionDAO.setLabelDAO(labelDAO);
		collectionDAO.setLabelingDAO(labelingDAO);
		collectionDAO.setSynonymDAO(synonymDAO);
		collectionDAO.setTermDAO(termDAO);
		termDAO.setBucketDAO(bucketDAO);
		termDAO.setCommentDAO(commentDAO);
		termDAO.setCollectionDAO(collectionDAO);
		labelDAO.setLabelingDAO(labelingDAO);
		labelDAO.setSynonymDAO(synonymDAO);
		labelingDAO.setLabelDAO(labelDAO);
		labelingDAO.setTermDAO(termDAO);
		synonymDAO.setTermDAO(termDAO);
		synonymDAO.setLabelingDAO(labelingDAO);
		communityDAO.setCollectionDAO(collectionDAO);
		communityDAO.setTermDAO(termDAO);
		communityDAO.setLabelDAO(labelDAO);
		communityDAO.setLabelingDAO(labelingDAO);
	}
	
	public BucketDAO getBucketDAO() {
		return bucketDAO;
	}

	public CollectionDAO getCollectionDAO() {
		return collectionDAO;
	}

	public TermDAO getTermDAO() {
		return termDAO;
	}

	public LabelDAO getLabelDAO() {
		return labelDAO;
	}

	public ContextDAO getContextDAO() {
		return contextDAO;
	}

	public SynonymDAO getSynonymDAO() {
		return synonymDAO;
	}

	public LabelingDAO getLabelingDAO() {
		return labelingDAO;
	}

	public OntologyDAO getOntologyDAO() {
		return ontologyDAO;
	}

	public CommentDAO getCommentDAO() {
		return commentDAO;
	}

	public CommunityDAO getCommunityDAO() {
		return communityDAO;
	}

}
