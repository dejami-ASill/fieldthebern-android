/*
 * Copyright (c) 2016 - Bernie 2016, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.berniesanders.fieldthebern.views;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import com.berniesanders.fieldthebern.R;
import com.berniesanders.fieldthebern.mortar.DaggerService;
import com.berniesanders.fieldthebern.screens.CollectionScreen;
import javax.inject.Inject;
import timber.log.Timber;

/**
 *
 */
public class CollectionView extends RecyclerView {

  @Inject
  CollectionScreen.Presenter presenter;

  public CollectionView(Context context) {
    super(context);
    injectSelf(context);
    setLayoutManager(context);
  }

  public CollectionView(Context context, AttributeSet attrs) {
    super(context, attrs);
    injectSelf(context);
    setLayoutManager(context);
  }

  public CollectionView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    injectSelf(context);
    setLayoutManager(context);
  }

  private void injectSelf(Context context) {
    DaggerService.<CollectionScreen.Component>getDaggerComponent(context,
        DaggerService.DAGGER_SERVICE).inject(this);
  }

  private void setLayoutManager(Context context) {
    GridLayoutManager gridLayoutManager =
        new GridLayoutManager(context, context.getResources().getInteger(R.integer.num_cols));
    setLayoutManager(gridLayoutManager);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    Timber.v("onFinishInflate");
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    presenter.takeView(this);
  }

  @Override
  protected void onDetachedFromWindow() {
    presenter.dropView(this);
    super.onDetachedFromWindow();
  }
}
