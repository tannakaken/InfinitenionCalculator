package jp.tannakaken.infinitenion.calculator;

import java.util.Random;

import android.os.AsyncTask;
import jp.tannakaken.infinitenion.R;
import jp.tannakaken.infinitenion.operand.Factory;
import jp.tannakaken.infinitenion.operand.ImaginaryFactory;
import jp.tannakaken.infinitenion.operand.BaseFieldFactory;
import jp.tannakaken.infinitenion.operand.VariableFactory;
import jp.tannakaken.infinitenion.operand.ResultantFactory;

/**
 * 入力された文字列を解釈し、実行し、出力される文字列を返すクラス。<br>
 * 実際の計算は、さまざまなクラスに委譲される。<br>
 * 具体的には、各種{@link Operand}のインスタンス生成は、それに対応した{@link Factory}のサブクラスに、委譲され、<br>
 * 演算の型チェックは{@link Operator}のインスタンスが行い、計算は{@link Constant}のインスタンスが自分で行う。<br>
 * これによって、このクラス自身は、非常に単純になっている。<br>
 * これは、{@link MainActivity}をクライアントとした、Facadeパターンの利用によるものである。
 * 
 * @author tannakaken
 * 
 * @see <a href="http://en.wikipedia.org/wiki/Facade_pattern">http://en.wikipedia.org/wiki/Facade_pattern</a>
 */
public class Calculator {
	
	/**
	 * 変数を作るFactory。
	 */
	private VariableFactory mVariableFactory = VariableFactory.getInstance();
	/**
	 * 定数を作るFactory。
	 */
	private ImaginaryFactory mConstantFactory = ImaginaryFactory.getInstance();
	/**
	 * 数を作るFactory。
	 */
	private BaseFieldFactory mNumberFactory = BaseFieldFactory.getInstance();
	/**
	 * スタックに溜まったオペランドから、オペレータに対応した操作で、新しいオペランドを作るFactory。
	 */
	private ResultantFactory mResultantFactory = ResultantFactory.getInstance();
	
	/**
	 * 
	 * @param aFormula 計算される式。
	 * @param aTask この計算を実行している非同期タスク。
	 * @return 式を計算した値の文字列。
	 * @throws CalculatorParseException パースの失敗
	 * @throws CalculatingException 計算の失敗
	 * @throws BackgroundProcessCancelledException バックグラウンド処理がキャンセルされたときの例外。
	 */
	public final String calc(final String aFormula, final AsyncTask<String, Void, String> aTask)
			throws CalculatorParseException, CalculatingException, BackgroundProcessCancelledException {
		try {
			String tTrimed =  aFormula.replaceFirst("^[□]*", "");
			if (tTrimed == "") { // イースターエッグ
				return randomAphorism();
			}
			String[] tTokens = tTrimed.split("[□]+");
			Factory.setTask(aTask);
			for (String tToken: tTokens) {
				if (aTask.isCancelled()) {
					throw new BackgroundProcessCancelledException();
				}
				if (mResultantFactory.getReady(tToken)) {
					mResultantFactory.calc();
				} else if (mVariableFactory.getReady(tToken)) {
					mVariableFactory.calc();
				} else if (mConstantFactory.getReady(tToken)) {
					mConstantFactory.calc();
				} else if (mNumberFactory.getReady(tToken)) {
					mNumberFactory.calc();
				} else {
					throw new CalculatorParseException(R.string.illegal_token, tToken);
				}
			}
			return Factory.getResult();
		} catch (CalculatorParseException | CalculatingException | BackgroundProcessCancelledException e) {
			throw e;
		} finally {
			Factory.clearStack();
		}
	}
	
	
	/**
	 * イースターエッグ用の格言集。
	 */
	private String[] mAphorisms = {"There's more than one way to do it.",
								   "The world is full of fascinating problems waiting to be solved.",
								   "If you have the right attitude, interesting problems will find you.",
								   "And now for something completely different ...",
								   "... and yes I said yes I will Yes."};
	/**
	 * イースターエッグ用の乱数。
	 */
	private Random mRandom = new Random();
	/**
	 * イースターエッグ用のメソッド。<br>
	 * 入力が空のとき、格言をランダムに返す。
	 * @return ランダムな格言
	 */
	private String randomAphorism() {
		return mAphorisms[mRandom.nextInt(mAphorisms.length)];
	}
	
}
