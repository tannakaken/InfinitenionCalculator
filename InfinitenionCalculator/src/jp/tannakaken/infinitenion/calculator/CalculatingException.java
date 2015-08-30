package jp.tannakaken.infinitenion.calculator;
/**
 * 計算の途中で起きた例外。
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
	 * @param aMessageId 例外の詳細を表すメッセージID。主に語順の間違い、arityの間違い等の文法違反。
	 */
	public CalculatingException(final int aMessageId) {
		super(aMessageId);
	}
	
}
