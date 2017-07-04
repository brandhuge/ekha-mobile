package org.mifos.selfserviceapp;

import android.content.Context;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.selfserviceapp.api.DataManager;
import org.mifos.selfserviceapp.models.accounts.loan.LoanWithAssociations;
import org.mifos.selfserviceapp.presenters.LoanRepaymentSchedulePresenter;
import org.mifos.selfserviceapp.ui.views.LoanRepaymentScheduleMvpView;
import org.mifos.selfserviceapp.util.RxSchedulersOverrideRule;
import org.mifos.selfserviceapp.utils.Constants;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import rx.Observable;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by dilpreet on 27/6/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class LoanRepaymentSchedulePresenterTest {

    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();

    @Mock
    Context context;

    @Mock
    DataManager dataManager;

    @Mock
    LoanRepaymentScheduleMvpView view;

    private LoanRepaymentSchedulePresenter presenter;
    private LoanWithAssociations loanWithRepaymentSchedule, loanWithEmptyRepaymentSchedule;

    @Before
    public void setUp() throws Exception {
        presenter = new LoanRepaymentSchedulePresenter(context, dataManager);
        presenter.attachView(view);

        loanWithRepaymentSchedule = FakeRemoteDataSource.getLoanAccountRepaymentSchedule();
        loanWithEmptyRepaymentSchedule = FakeRemoteDataSource.
                getLoanAccountEmptyRepaymentSchedule();

        when(context.getString(R.string.error_fetching_repayment_schedule)).
                thenReturn("Failed To fetch repayment schedule");

    }

    @After
    public void tearDown() throws Exception {
        presenter.detachView();
    }

    @Test
    public void testLoadLoanAccountDetails() throws Exception {
        when(dataManager.getLoanWithAssociations(Constants.REPAYMENT_SCHEDULE, 29)).
                thenReturn(Observable.just(loanWithRepaymentSchedule));

        presenter.loanLoanWithAssociations(29);

        verify(view).showProgress();
        verify(view).hideProgress();
        verify(view).showLoanRepaymentSchedule(loanWithRepaymentSchedule);
        verify(view, never()).showEmptyRepaymentsSchedule(null);
        verify(view, never()).showError(context.
                getString(R.string.error_fetching_repayment_schedule));

    }

    @Test
    public void testLoadLoanAccountDetailsEmpty() throws Exception {
        when(dataManager.getLoanWithAssociations(Constants.REPAYMENT_SCHEDULE, 29)).
                thenReturn(Observable.just(loanWithEmptyRepaymentSchedule));

        presenter.loanLoanWithAssociations(29);

        verify(view).showProgress();
        verify(view).hideProgress();
        verify(view).showEmptyRepaymentsSchedule(loanWithEmptyRepaymentSchedule);
        verify(view, never()).showLoanRepaymentSchedule(null);
        verify(view, never()).showError(context.
                getString(R.string.error_fetching_repayment_schedule));

    }


    @Test
    public void testLoadLoanAccountDetailsFails() throws Exception {
        when(dataManager.getLoanWithAssociations(Constants.REPAYMENT_SCHEDULE, 29)).
                thenReturn(Observable.<LoanWithAssociations>error(new RuntimeException()));

        presenter.loanLoanWithAssociations(29);

        verify(view).showProgress();
        verify(view).hideProgress();
        verify(view).showError(context.getString(R.string.error_fetching_repayment_schedule));
        verify(view, never()).showEmptyRepaymentsSchedule(null);
        verify(view, never()).showLoanRepaymentSchedule(null);

    }
}
