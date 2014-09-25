package jp.tannakaken.infinitenion.calculator;
/**
 * パースの途中で起きた例外。
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
	 * @param aMessageId 例外の詳細を表すメッセージID。入力文字列をtokenに分解した際、受け入れ不可能なtokenがあったこと伝える。。
	 * @param aToken 受け入れ不可能なトークン。
	 */
	public CalculatorParseException(final int aMessageId, final String aToken) {
		super(aMessageId, aToken);
	}

	
}
