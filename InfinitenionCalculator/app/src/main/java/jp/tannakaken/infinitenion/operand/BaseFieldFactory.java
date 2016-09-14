package jp.tannakaken.infinitenion.operand;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.tannakaken.infinitenion.R;
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
	private static String mIntegerRegexp = "(^0$|^-?[1-9]\\d*)";
	/**
	 * 整数を表すパターン。
	 */
	private static Pattern mIntegerPattern = Pattern.compile(mIntegerRegexp);
	/**
	 * 実数を表す正規表現。
	 */
	private static String mRealRegexp = "(^(0|(-?[1-9]\\d*))(\\.\\d*)?)";
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
	 * 実数モードのとき、与えられたトークンが整数かどうかを判定。
	 */
	private boolean mIsInteger;
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
		if (super.isCanceled()) {
			throw new BackgroundProcessCancelledException();
		}
		if (aInput.equals("TheAnswerToTheLifeTheUniverseAndEverything")) {
			mToken = String.valueOf(aInput.length());
			mIsInteger = true;
			return "";
		}
		// 検査の順番に注意　自然数かどうかをチェックしてから、実数かどうかをチェックする。
		if (isReal()) {
			Matcher tMatcher = mIntegerPattern.matcher(aInput);
			if (tMatcher.find()) {
				mIsInteger = true;
				mToken = tMatcher.group();
				return aInput.substring(tMatcher.end());
			}
			tMatcher = mRealPattern.matcher(aInput);
			if (tMatcher.find()) {
				mIsInteger = false;
				mToken = tMatcher.group();
				return aInput.substring(tMatcher.end());
			}
			return null;
		} else {
			Matcher tMatcher = mIntegerPattern.matcher(aInput);
			if (tMatcher.find()) {
				mToken = tMatcher.group();
				return aInput.substring(tMatcher.end());
			}
			if (mRealPattern.matcher(aInput).find()) {
				throw new CalculatorParseException(R.string.radix_point_in_rational_mode, aInput);
			}
			return null;
		}
	}

	@Override
	public void calc() throws CalculatingException {
		if (isReal()) {
			if (mIsInteger) {
				getStack().push(new Real(new BigDecimal(mToken)));
			} else {
				getStack().push(new Real(new BigDecimal(mToken), Prefs.getScale(getContext())));
			}
		} else {
			getStack().push(new Rational(new BigInteger(mToken)));
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
			return new Rational(BigInteger.ONE);	
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