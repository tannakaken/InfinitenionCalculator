package jp.tannakaken.infinitenion.operand;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.tannakaken.infinitenion.calculator.BackgroundProcessCancelledException;
import jp.tannakaken.infinitenion.calculator.CalculatingException;
import jp.tannakaken.infinitenion.calculator.CalculatorParseException;
import jp.tannakaken.infinitenion.gui.Prefs;
/**
 * トークンから数を生成するFactory。<br/>
 * 必ずトークンが需要可能か検査してから、インスタンスを生成する工程を一本化するために、Factory Methodパターンを使用。<br/>
 * 一つしか必要ないので、Singletonパターンを使う。
 * @author tannakaken
 * @see
 * <a href="http://en.wikipedia.org/wiki/Factory_method_pattern">
 * http://en.wikipedia.org/wiki/Factory_method_pattern</a><br/>
 * <a href="http://en.wikipedia.org/wiki/Singleton_pattern">http://en.wikipedia.org/wiki/Singleton_pattern</a>
 *
 */
public final class BaseFieldFactory extends Factory {

	/**
	 * 整数を表す正規表現。
	 */
	private static String mIntegerRegexp = "(^-?(0|[1-9]\\d*))";
	/**
	 * 整数を表すパターン。
	 */
	private static Pattern mIntegerPattern = Pattern.compile(mIntegerRegexp);
	/**
	 * 実数を表す正規表現。
	 */
	private static String mRealRegexp = "(^-?(0|([1-9]\\d*))(\\.\\d*)?)";
	/**
	 * 実数を表すパターン。
	 */
	private static Pattern mRealPattern = Pattern.compile(mRealRegexp);
	/**
	 * {@link BaseFieldFactory}の<a href="http://en.wikipedia.org/wiki/Singleton_pattern">singleton</a>。
	 */
	private static BaseFieldFactory mSingleton = new BaseFieldFactory();
	/**
	 * {@link Rational}または{@link Real}を作るための、受け入れ可能なトークン。
	 */
	private String mToken;
	/**
	 * 
	 * @return {@link BaseFieldFactory}の<a href="http://en.wikipedia.org/wiki/Singleton_pattern">singleton</a>
	 */
	public static BaseFieldFactory getInstance() {
		return mSingleton;
	}
	
	@Override
	public String getReady(final String aInput)
			throws CalculatorParseException, BackgroundProcessCancelledException {
		if (Factory.isCanceled()) {
			throw new BackgroundProcessCancelledException();
		}
		if (aInput.equals("TheAnswerToTheLifeTheUniverseAndEverything")) {
			mToken = String.valueOf(aInput.length());
			return "";
		}
		Matcher tMatcher = isReal() ? mRealPattern.matcher(aInput) : mIntegerPattern.matcher(aInput);
		if (tMatcher.find()) {
			mToken = tMatcher.group();
			return aInput.substring(tMatcher.end());
		}
		return null;
	}

	/**
	 * ゼロを表す正規表現。
	 */
	private static String mZeroRegexp = "-?0+(\\.0+)?";
	/**
	 * ゼロを表すパターン。
	 */
	private static Pattern mZeroPattern = Pattern.compile(mZeroRegexp);
	@Override
	public void calc() throws CalculatingException {
		if (mZeroPattern.matcher(mToken).matches()) {
			getStack().push(Zero.ZERO);
		} else if (isReal()) {
			getStack().push(new Real(new BigDecimal(mToken), Prefs.getScale(getContext())));
		} else {
			getStack().push(new Rational(new BigDecimal(mToken)));
		}
	}
	
	/**
	 * 
	 * @return 現在のモードの1。
	 */
	Constant getOne() throws CalculatingException {
		if (isReal()) {
			return new Real(BigDecimal.ONE);
		} else {
			return new Rational(BigDecimal.ONE);
		}
	}
	/**
	 * 
	 * @return 現在実数モードかどうか。
	 */
	private static boolean isReal() {
		return Prefs.getIsReal(getContext());
	}
	
	
	
}
