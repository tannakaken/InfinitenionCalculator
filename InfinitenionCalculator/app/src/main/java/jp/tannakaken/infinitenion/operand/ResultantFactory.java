package jp.tannakaken.infinitenion.operand;

import java.math.BigInteger;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.tannakaken.infinitenion.R;
import jp.tannakaken.infinitenion.calculator.BackgroundProcessCancelledException;
import jp.tannakaken.infinitenion.calculator.CalculatingException;
/**
 * スタックに溜まった{@link Operand}を作って、オペレータに合わせて計算し、
 * 新しい{@link Operand}をスタックにつむ。<br>
 * 一つしかインスタンスが必要ないので、Singletonパターンを使う。<br>
 * また、{@link Constant}の{@link Operand#getHeight()}の値が0で、
 * {@link Zero}のインスタンスでないときに、{@link Operand#add(Operand)}、
 * {@link Operand#mul(Operand)}、{@link Operand#negate()}、{@link Operand#inv()}、
 * を実装すれば、全ての演算が実装されるのは、一種のTemplate Methodパターンである。
 * @author tannakaken
 * 
 * @see
 * <a href="http://en.wikipedia.org/wiki/Singleton_pattern">http://en.wikipedia.org/wiki/Singleton_pattern</a><br>
 * <a href="http://en.wikipedia.org/wiki/Template_method_pattern">
 * http://en.wikipedia.org/wiki/Template_method_pattern</a>
 */
public final class ResultantFactory extends Factory {
	/**
	 * {@link Constant}を作るため。 
	 */
	private static ImaginaryFactory mImaginaryFactory = ImaginaryFactory.getInstance();
	
	/**
	 * {@link Operator}と、それをを表すトークンの対応。
	 */
	private static final Map<String, Operator> OPERATORS = new HashMap<>(16, 1.0f);
	/**
	 * 一つしか必要ないので<a href="http://en.wikipedia.org/wiki/Singleton_pattern">Singleton</a>。
	 * 
	 */
	private static ResultantFactory mSingleton = new ResultantFactory();
	/**
	 * 次に行う{@link Operator}。
	 */
	private Operator mOperator; 
	
	/**
	 * {@link Operator#ASSOC}のarityは3。
	 */
	private static final int ASSOC_ARRITY = 3;

	/**
	 * 演算子をあらわす記号かどうかを判定する正規表現。
	 */
	private static String mRegex = "(^(\\+|\\-|\\*|/|\\^|<=|norm|conj|negate|inv|commu|assoc|normed))";
	/**
	 * 正規表現をコンパイル。
	 */
	private static Pattern mPattern = Pattern.compile(mRegex);

	static {
		OPERATORS.put("+", Operator.ADD);
		OPERATORS.put("-", Operator.SUB);
		OPERATORS.put("*", Operator.MUL);
		OPERATORS.put("/", Operator.DIV);
		OPERATORS.put("^", Operator.POW);
		OPERATORS.put("<=", Operator.SUBSTITUTION);
		OPERATORS.put("norm", Operator.NORM);
		OPERATORS.put("conj", Operator.CONJ);
		OPERATORS.put("negate", Operator.NEGATE);
		OPERATORS.put("inv", Operator.INV);
		OPERATORS.put("commu", Operator.COMMU);
		OPERATORS.put("assoc", Operator.ASSOC);
		OPERATORS.put("normed", Operator.NORMED);
	}
	/**
	 * 外部からインスタンス化できないようにしておく。
	 */
	private ResultantFactory() {
	}
	/**
	 * 
	 * @return {@link ResultantFactory}の<a href="http://en.wikipedia.org/wiki/Singleton_pattern">Singleton</a>。
	 * 
	 */
	public static ResultantFactory getInstance() {
		return mSingleton;
	}
	@Override
	public String getReady(final String aInput) throws BackgroundProcessCancelledException {
		if (Factory.isCanceled()) {
			throw new BackgroundProcessCancelledException();
		}
		Matcher tMatcher = mPattern.matcher(aInput);
		if (tMatcher.find()) {
			mOperator = OPERATORS.get(tMatcher.group());
			return aInput.substring(tMatcher.end());
		}
		return null;
	}
	@Override
	public void calc() throws CalculatingException, BackgroundProcessCancelledException {
		Deque<Operand> tStack = getStack();
		switch (mOperator) {
		case ADD:
			tStack.push(add());
			return;
		case SUB:
			tStack.push(sub());
			return;
		case MUL:
			tStack.push(mul());
			return;
		case DIV:
			tStack.push(div());
			return;
		case POW:
			tStack.push(pow());
			return;
		case SUBSTITUTION:
			tStack.push(substitution());
			return;
		case NORM:
			tStack.push(norm());
			return;
		case CONJ:
			tStack.push(conj());
			return;
		case NEGATE:
			tStack.push(negate());
			return;
		case INV:
			tStack.push(inv());
			return;
		case COMMU:
			tStack.push(commu());
			return;
		case ASSOC:
			tStack.push(assoc());
			return;
		case NORMED:
			tStack.push(normed());
			return;
		default:
		}
	}
	
	/**
	 * Javaの引数の評価順序は左から順にである。
	 * @return {@link Factory#getStack()}から取り出された二つの値の和
	 * @throws CalculatingException {@link Factory#getStack()}に積まれた+のための引数が少なすぎる。
	 * @throws BackgroundProcessCancelledException バックグラウンド処理がキャンセルされたときの例外。
	 */
	private Constant add() throws CalculatingException, BackgroundProcessCancelledException {
		if (Factory.isCanceled()) {
			throw new BackgroundProcessCancelledException();
		}
		Deque<Operand> tStack = getStack();
		if (tStack.size() < 2) {
			throw new CalculatingException(R.string.add_arity_error);
		}
		Operand tOperand2 = tStack.pop();
		Operand tOperand1 = tStack.pop();
		if (tOperand1.isZero()) {
			return tOperand2.getInterior();
		}
		if (tOperand2.isZero()) {
			return tOperand1.getInterior();
		}
		int tHeight1 = tOperand1.getHeight();
		int tHeight2 = tOperand2.getHeight();
		if (tHeight1 > tHeight2) {
			// 最後に出されるもの。
			tStack.push(tOperand1.getImag());
			// 演算されるもの
			tStack.push(tOperand1.getReal());
			tStack.push(tOperand2);
			return mImaginaryFactory.mixRealAndImaginary(add(), tStack.pop().getInterior(), tHeight1);
		} else if (tHeight2 > tHeight1) {
			// 最後に出されるもの。
			tStack.push(tOperand2.getImag());
			// 演算されるもの。
			tStack.push(tOperand1);
			tStack.push(tOperand2.getReal());
			return mImaginaryFactory.mixRealAndImaginary(add(), tStack.pop().getInterior(), tHeight2);
		} else if (tHeight1 == 0) {
			return tOperand1.add(tOperand2);
		} else {
			// 最後に演算されるもの
			tStack.push(tOperand1.getImag());
			tStack.push(tOperand2.getImag());
			// 最初に演算されるもの
			tStack.push(tOperand1.getReal());
			tStack.push(tOperand2.getReal());
			return mImaginaryFactory.mixRealAndImaginary(add(), add(), tHeight1);
		}
	}
	/**
	 * Javaの引数の評価順序は左から順にである。
	 * @return {@link Factory#getStack()}から取り出された二つの値の積
	 * @throws CalculatingException {@link Factory#getStack()}に積まれた*のための引数が少なすぎる。
	 * @throws BackgroundProcessCancelledException バックグラウンド処理がキャンセルされたときの例外。
	 */
	private Constant mul() throws CalculatingException, BackgroundProcessCancelledException {
		if (Factory.isCanceled()) {
			throw new BackgroundProcessCancelledException();
		}
		Deque<Operand> tStack = getStack();
		if (tStack.size() < 2) {
			throw new CalculatingException(R.string.multiply_arity_error);
		}
		Operand tOperand2 = tStack.pop();
		Operand tOperand1 = tStack.pop();
		if (tOperand1.isZero() || tOperand2.isZero()) {
			return Zero.ZERO;
		}
		int tHeight1 = tOperand1.getHeight();
		int tHeight2 = tOperand2.getHeight();
		// 高さを比べて、0による掛け算を省略する。
		// (p,q)(r,s)=(p r - s^* q, s p + q r^*)である。
		// 単純に分配則を適用しないように注意。
		if (tHeight1 > tHeight2) { // 相手のほうが次元が低いとき。
			// 最後に演算されるもの
			tStack.push(tOperand1.getImag());
			tStack.push(tOperand2);
			tStack.push(conj());
			// 最初に演算されるもの
			tStack.push(tOperand1.getReal());
			tStack.push(tOperand2);
			return mImaginaryFactory.mixRealAndImaginary(mul(), mul(), tHeight1);
		} else if (tHeight2 > tHeight1) { // 相手のほうが次元が高いとき。
			// 最後に演算されるもの
			tStack.push(tOperand2.getImag());
			tStack.push(tOperand1);
			// 最初に演算されるもの
			tStack.push(tOperand1);
			tStack.push(tOperand2.getReal());
			return mImaginaryFactory.mixRealAndImaginary(mul(), mul(), tHeight2);
		} else if (tHeight1 == 0) {
			return tOperand1.mul(tOperand2);
		} else {
			// 最後に演算されるもの
			tStack.push(tOperand2.getImag());
			tStack.push(tOperand1.getReal());
			tStack.push(mul()); // s pを格納
			tStack.push(tOperand1.getImag());
			tStack.push(tOperand2.getReal());
			tStack.push(conj());
			tStack.push(mul()); // q r^*を格納
			// 最初に演算されるもの。
			tStack.push(tOperand1.getReal());
			tStack.push(tOperand2.getReal());
			tStack.push(mul()); // p rを格納
			tStack.push(tOperand2.getImag());
			tStack.push(conj());
			tStack.push(tOperand1.getImag());
			tStack.push(mul());
			tStack.push(negate()); // s^* qを格納
			return mImaginaryFactory.mixRealAndImaginary(add(), add(), tHeight1);
		}
	}
	
	/**
	 * Javaの引数の評価順序は左から順にである。
	 * @return {@link Factory#getStack()}から取り出された二つの値の累乗
	 * @throws CalculatingException {@link Factory#getStack()}に積まれた^のための引数が少なすぎる。<br>
	 * または、二つ目の引数が整数でない。
	 * @throws BackgroundProcessCancelledException バックグラウンド処理がキャンセルされたときの例外。
	 */
	private Constant pow() throws CalculatingException, BackgroundProcessCancelledException {
		Deque<Operand> tStack = getStack();
		if (tStack.size() < 2) {
			throw new CalculatingException(R.string.power_arity_error);
		}
		Operand tOperand2 = tStack.pop();
		if (!tOperand2.isInteger()) {
			throw new CalculatingException(R.string.power_not_integer_error);
		}
		BigInteger tPow = tOperand2.getInteger();
		Operand tBase = tStack.pop();
		Constant tResult = BaseFieldFactory.getInstance().getOne();
		if (tPow.compareTo(BigInteger.ZERO) < 0) {
			tPow = tPow.negate();
			tStack.push(tBase);
			tBase = inv();
		}
		while (tPow.compareTo(BigInteger.ZERO) > 0) {
			if (Factory.isCanceled()) {
				throw new BackgroundProcessCancelledException();
			}
			if (tPow.getLowestSetBit() == 0) { // ２進数表記で一番右が埋まっている、ということはこれが奇数であることを意味する。
				tStack.push(tBase);
				tStack.push(tResult);
				tResult = mul();
				tPow = tPow.subtract(BigInteger.ONE);
			} else {
				tStack.push(tBase);
				tStack.push(tBase);
				tBase = mul();
				tPow = tPow.shiftRight(1);
			}
		}
		return tResult;
	}
	/**
	 * 
	 * @return {@link Factory#getStack()}から取り出された値の符号を反転したもの。
	 * @throws CalculatingException {@link Factory#getStack()}に積まれたminusのための引数が少なすぎる。
	 * @throws BackgroundProcessCancelledException バックグラウンド処理がキャンセルされたときの例外。
	 */
	private Constant negate() throws CalculatingException, BackgroundProcessCancelledException {
		if (Factory.isCanceled()) {
			throw new BackgroundProcessCancelledException();
		}
		Deque<Operand> tStack = getStack();
		if (tStack.size() < 1) {
			throw new CalculatingException(R.string.negate_arity_error);
		}
		Operand tOperand = tStack.pop();
		int tHeight = tOperand.getHeight();
		if (tHeight == 0) {
			return tOperand.negate();
		} else {
			tStack.push(tOperand.getImag());
			tStack.push(tOperand.getReal());
			return mImaginaryFactory.mixRealAndImaginary(negate(), negate(), tHeight);
		}
	}
	/**
	 * Javaの引数の評価順序は左から順にである。
	 * @return {@link Factory#getStack()}から取り出された二つの値の差
	 * @throws CalculatingException {@link Factory#getStack()}に積まれた-のための引数が少なすぎる。
	 * @throws BackgroundProcessCancelledException バックグラウンド処理がキャンセルされたときの例外。
	 */
	private Constant sub() throws CalculatingException, BackgroundProcessCancelledException {
		if (Factory.isCanceled()) {
			throw new BackgroundProcessCancelledException();
		}
		Deque<Operand> tStack = getStack();
		if (tStack.size() < 2) {
			throw new CalculatingException(R.string.substruct_arity_error);
		}
		tStack.push(negate());
		return add();
	}
	/**
	 * 
	 * @return {@link Factory#getStack()}から取り出された値の共役。
	 * @throws CalculatingException {@link Factory#getStack()}に積まれたconjのための引数が少なすぎる。
	 * @throws BackgroundProcessCancelledException バックグラウンド処理がキャンセルされたときの例外。
	 */
	private Constant conj() throws CalculatingException, BackgroundProcessCancelledException {
		if (Factory.isCanceled()) {
			throw new BackgroundProcessCancelledException();
		}
		Deque<Operand> tStack = getStack();
		if (tStack.size() < 1) {
			throw new CalculatingException(R.string.conj_arity_error);
		}
		Operand tOperand = tStack.pop();
		int tHeight = tOperand.getHeight();
		if (tHeight == 0) {
			return tOperand.getInterior();
		} else {
			tStack.push(tOperand.getImag());
			tStack.push(tOperand.getReal());
			return mImaginaryFactory.mixRealAndImaginary(conj(), negate(), tHeight);
		}
	}
	/**
	 * 
	 * @return {@link Factory#getStack()}から取り出された値のノルム
	 * @throws CalculatingException {@link Factory#getStack()}に積まれたnormのための引数が少なすぎる。
	 * @throws BackgroundProcessCancelledException バックグラウンド処理がキャンセルされたときの例外。
	 */
	private Constant norm() throws CalculatingException, BackgroundProcessCancelledException {
		if (Factory.isCanceled()) {
			throw new BackgroundProcessCancelledException();
		}
		Deque<Operand> tStack = getStack();
		if (tStack.size() < 1) {
			throw new CalculatingException(R.string.norm_arity_error);
		}
		Operand tOperand = tStack.pop();
		if (tOperand.getHeight() == 0) {
			return tOperand.mul(tOperand);
		} else {
			tStack.push(tOperand.getImag());
			tStack.push(norm());
			tStack.push(tOperand.getReal());
			tStack.push(norm());
			return tStack.pop().add(tStack.pop());
		}
	}
	/**
	 * 
	 * @return {@link Factory#getStack()}から取り出された値の逆数。
	 * @throws CalculatingException {@link Factory#getStack()}に積まれたinvのための引数が少なすぎる。
	 * @throws BackgroundProcessCancelledException バックグラウンド処理がキャンセルされたときの例外。
	 */
	private Constant inv() throws CalculatingException, BackgroundProcessCancelledException {
		if (Factory.isCanceled()) {
			throw new BackgroundProcessCancelledException();
		}
		Deque<Operand> tStack = getStack();
		if (tStack.size() < 1) {
			throw new CalculatingException(R.string.inv_arity_error);
		}
		Operand tOperand = tStack.pop();
		if (tOperand.getHeight() == 0) {
			return tOperand.inv();
		}
		tStack.push(tOperand);
		tStack.push(conj());
		tStack.push(tOperand);
		tStack.push(norm());
		return div();
	}
	/**
	 * Javaの引数の評価順序は左から順にである。
	 * @return {@link Factory#getStack()}から取り出された二つの値の商
	 * @throws CalculatingException {@link Factory#getStack()}に積まれた/のための引数が少なすぎる。
	 * @throws BackgroundProcessCancelledException バックグラウンド処理がキャンセルされたときの例外。
	 */
	private Constant div() throws CalculatingException, BackgroundProcessCancelledException {
		if (Factory.isCanceled()) {
			throw new BackgroundProcessCancelledException();
		}
		Deque<Operand> tStack = getStack();
		if (tStack.size() < 2) {
			throw new CalculatingException(R.string.divide_arity_error);
		}
		Operand tOperand2 = tStack.pop();
		if (tOperand2.getHeight() == 0) {
			if (tOperand2.isZero()) {
				throw new CalculatingException(R.string.divide_by_zero);
			}
			Operand tOperand1 = tStack.pop();
			int tHeight = tOperand1.getHeight();
			if (tHeight == 0) {
				return tOperand1.div(tOperand2);
			}
			// 最後に演算されるもの
			tStack.push(tOperand1.getImag());
			tStack.push(tOperand2);
			// 最初に演算されるもの。
			tStack.push(tOperand1.getReal());
			tStack.push(tOperand2);
			return mImaginaryFactory.mixRealAndImaginary(div(), div(), tHeight);
		}
		tStack.push(inv());
		return mul();
	}
	/**
	 * Javaの引数の評価順序は左から順にである。
	 * @return {@link Factory#getStack()}から取り出された二つの値のa,bのab-ba
	 * @throws CalculatingException {@link Factory#getStack()}に積まれたcommuのための引数が少なすぎる。
	 * @throws BackgroundProcessCancelledException バックグラウンド処理がキャンセルされたときの例外。
	 */
	private Constant commu() throws CalculatingException, BackgroundProcessCancelledException {
		if (Factory.isCanceled()) {
			throw new BackgroundProcessCancelledException();
		}
		Deque<Operand> tStack = getStack();
		if (tStack.size() < 2) {
			throw new CalculatingException(R.string.commu_arity_error);
		}
		Operand tOperand2 = tStack.pop();
		Operand tOperand1 = tStack.peek();
		tStack.push(tOperand2);
		tStack.push(mul());
		tStack.push(tOperand2);
		tStack.push(tOperand1);
		tStack.push(mul());
		return sub();
	}
	/**
	 * Javaの引数の評価順序は左から順にである。
	 * @return {@link Factory#getStack()}から取り出された三つの値の(ab)c-a(bc)
	 * @throws CalculatingException {@link Factory#getStack()}に積まれたassocのための引数が少なすぎる。
	 * @throws BackgroundProcessCancelledException バックグラウンド処理がキャンセルされたときの例外。
	 */
	private Constant assoc() throws CalculatingException, BackgroundProcessCancelledException {
		if (Factory.isCanceled()) {
			throw new BackgroundProcessCancelledException();
		}
		Deque<Operand> tStack = getStack();
		if (tStack.size() < ASSOC_ARRITY) {
			throw new CalculatingException(R.string.assoc_arity_error);
		}
		Operand tOperand3 = tStack.pop();
		Operand tOperand2 = tStack.pop();
		Operand tOperand1 = tStack.peek();
		tStack.push(tOperand2);
		tStack.push(mul());
		tStack.push(tOperand3);
		tStack.push(mul());
		tStack.push(tOperand1);
		tStack.push(tOperand2);
		tStack.push(tOperand3);
		tStack.push(mul());
		tStack.push(mul());
		return sub();
	}
	/**
	 * Javaの引数の評価順序は左から順にである。
	 * @return {@link Factory#getStack()}から取り出された二つの値a,bの|ab|-|a||b|
	 * @throws CalculatingException {@link Factory#getStack()}に積まれたnormedのための引数が少なすぎる。
	 * @throws BackgroundProcessCancelledException バックグラウンド処理がキャンセルされたときの例外。
	 */
	private Constant normed() throws CalculatingException, BackgroundProcessCancelledException {
		if (Factory.isCanceled()) {
			throw new BackgroundProcessCancelledException();
		}
		Deque<Operand> tStack = getStack();
		if (tStack.size() < 2) {
			throw new CalculatingException(R.string.normed_arity_error);
		}
		Operand tOperand2 = tStack.pop();
		Operand tOperand1 = tStack.peek();
		tStack.push(tOperand2);
		tStack.push(mul());
		tStack.push(norm());
		tStack.push(tOperand1);
		tStack.push(norm());
		tStack.push(tOperand2);
		tStack.push(norm());
		tStack.push(mul());
		return sub();
	}
	/**
	 * Javaの引数の評価順序は左から順にである。
	 * @return {@link Factory#getStack()}から取り出された二つの値の二つ目に、一つ目を代入する。
	 * @throws CalculatingException {@link Factory#getStack()}に積まれた<=のための引数が少なすぎる。
	 * @throws BackgroundProcessCancelledException バックグラウンド処理がキャンセルされたときの例外。
	 */
	private Operand substitution() throws CalculatingException, BackgroundProcessCancelledException {
		if (Factory.isCanceled()) {
			throw new BackgroundProcessCancelledException();
		}
		Deque<Operand> tStack = getStack();
		if (tStack.size() < 2) {
			throw new CalculatingException(R.string.substitution_arity_error);
		}
		Operand tOperand2 = tStack.pop();
		Operand tOperand1 = tStack.pop();
		tOperand1.setInterior(tOperand2.getInterior());
		return tOperand1;
	}
}
