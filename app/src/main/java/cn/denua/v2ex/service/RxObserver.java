/*
 * Copyright (c) 2018 denua.
 */

package cn.denua.v2ex.service;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/*
 * 通用请求响应观察者，这里会预处理一些错误响应， 以及捕捉子类中处理响应结果时发生的错误。
 *
 * @author denua
 */
public abstract class RxObserver<T> implements Observer<T> {

    private BaseService<?> mBaseService;

    public RxObserver() {
    }

    RxObserver(BaseService<?> baseService) {
        this.mBaseService = baseService;
    }

    @Override
    public void onSubscribe(Disposable d) {
        if (mBaseService != null) {
            mBaseService.onStartRequest();
        }
    }

    public void onNext(T t) {
        if (mBaseService != null && mBaseService.isCanceled()) return;

        if (t == null) {
            _onError(ErrorEnum.ERR_EMPTY_RESPONSE.getReadable());
            return;
        }
        if (t instanceof String) {
            if (((String) t).contains(ErrorEnum.ERR_PAGE_NEED_LOGIN.getPattern())
                    || ((String) t).contains(ErrorEnum.ERR_PAGE_NEED_LOGIN0.getPattern())) {
                _onError(ErrorEnum.ERR_PAGE_NEED_LOGIN.getReadable());
                return;
            }
        }
        try {
            _onNext(t);
        } catch (V2exException e) {
            e.printStackTrace();
            _onError(e.getMsg());
        } catch (NullPointerException e) {
            e.printStackTrace();
            _onError(e.getMessage());
        }
    }

    /**
     * 在这里处理请求层次的错误，例如网络错误，服务器错误等
     *
     * @param e 异常
     */
    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        _onError(e.getMessage());
    }

    @Override
    public void onComplete() {

    }

    public abstract void _onNext(T t);

    /**
     * 在这里处理 处理响应时发生的异常
     *
     * @param msg 返回给 view 的错误消息
     */
    public void _onError(String msg) {
        if (mBaseService != null) {
            mBaseService.returnFailed(msg);
        }
    }
}
