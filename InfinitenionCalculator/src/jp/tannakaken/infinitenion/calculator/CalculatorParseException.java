package jp.tannakaken.infinitenion.calculator;
/**
 * �p�[�X�̓r���ŋN������O�B
 * @author tannakaken
 *
 */
public class CalculatorParseException extends CalculatorException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 * @param aMessageId ��O�̏ڍׂ�\�����b�Z�[�WID�B���͕������token�ɕ��������ہA�󂯓���s�\��token�����������Ɠ`����B�B
	 * @param aToken �󂯓���s�\�ȃg�[�N���B
	 */
	public CalculatorParseException(final int aMessageId, final String aToken) {
		super(aMessageId, aToken);
	}

	
}
