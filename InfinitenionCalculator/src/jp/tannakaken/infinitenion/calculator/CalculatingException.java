package jp.tannakaken.infinitenion.calculator;
/**
 * �v�Z�̓r���ŋN������O�B
 * @author tannakaken
 *
 */
public class CalculatingException extends CalculatorException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 * @param aMessageId ��O�̏ڍׂ�\�����b�Z�[�WID�B��Ɍꏇ�̊ԈႢ�Aarity�̊ԈႢ���̕��@�ᔽ�B
	 */
	public CalculatingException(final int aMessageId) {
		super(aMessageId);
	}
	
}
