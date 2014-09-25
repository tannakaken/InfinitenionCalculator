/**
 * 入力をトークンに分解して、{@link jp.tannakaken.infinitenion.operand.Factory}のサブクラスに受け渡す。package。<br>
 * MVCでいうControllerである。<br>
 * 実際の計算は,{@link jp.tannakaken.infinitenion.operand.Factory}のサブクラスや、
 * {@link jp.tannakaken.infinitenion.operand.Operand}の実装クラスが行うので、
 * Controllerは比較的簡単である。
 * @author tannakaken
 * @see
 * <a href="http://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller">
 * http://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller</a>
 */
package jp.tannakaken.infinitenion.calculator;