package org.ovirt.engine.ui.uicommonweb.models.users;

import java.util.ArrayList;

import javax.inject.Inject;

import org.ovirt.engine.core.common.action.ActionParametersBase;
import org.ovirt.engine.core.common.action.ActionType;
import org.ovirt.engine.core.common.action.PermissionsOperationsParameters;
import org.ovirt.engine.core.common.businessentities.Permission;
import org.ovirt.engine.core.common.businessentities.aaa.DbUser;
import org.ovirt.engine.core.common.queries.IdQueryParameters;
import org.ovirt.engine.core.common.queries.QueryReturnValue;
import org.ovirt.engine.core.common.queries.QueryType;
import org.ovirt.engine.ui.frontend.AsyncCallback;
import org.ovirt.engine.ui.frontend.Frontend;
import org.ovirt.engine.ui.uicommonweb.UICommand;
import org.ovirt.engine.ui.uicommonweb.auth.ApplicationGuids;
import org.ovirt.engine.ui.uicommonweb.help.HelpTag;
import org.ovirt.engine.ui.uicommonweb.models.ConfirmationModel;
import org.ovirt.engine.ui.uicommonweb.models.configure.PermissionListModel;
import org.ovirt.engine.ui.uicompat.ConstantsManager;

import com.google.inject.Provider;

public class UserPermissionListModel extends PermissionListModel<DbUser> {

    @Inject
    public UserPermissionListModel(Provider<AdElementListModel> adElementListModelProvider) {
        super(adElementListModelProvider);
        setTitle(ConstantsManager.getInstance().getConstants().permissionsTitle());
        setHelpTag(HelpTag.permissions);
        setHashName("permissions"); // $//$NON-NLS-1$

        updateActionAvailability();
    }

    @Override
    protected void onEntityChanged() {
        super.onEntityChanged();
        getSearchCommand().execute();
    }

    @Override
    public void search() {
        if (getEntity() != null) {
            super.search();
        }
    }

    @Override
    protected void syncSearch() {
        if (getEntity() == null) {
            return;
        }
        IdQueryParameters mlaParams = new IdQueryParameters(getEntity().getId());
        mlaParams.setRefresh(getIsQueryFirstTime());

        Frontend.getInstance().runQuery(QueryType.GetPermissionsOnBehalfByAdElementId, mlaParams, new AsyncQuery<>((AsyncCallback<QueryReturnValue>) returnValue -> {
            ArrayList<Permission> list = returnValue.getReturnValue();
            ArrayList<Permission> newList = new ArrayList<>();
            for (Permission permission : list) {
                if (!permission.getRoleId().equals(ApplicationGuids.quotaConsumer.asGuid())) {
                    newList.add(permission);
                }
            }
            setItems(newList);
        }));

        setIsQueryFirstTime(false);

    }

    public void remove() {
        if (getWindow() != null) {
            return;
        }

        ConfirmationModel model = new ConfirmationModel();
        setWindow(model);
        model.setTitle(ConstantsManager.getInstance().getConstants().removePermissionTitle());
        model.setHelpTag(HelpTag.remove_permission);
        model.setHashName("remove_permission"); //$NON-NLS-1$
        model.setItems(new ArrayList<>(getSelectedItems()));

        UICommand tempVar = UICommand.createDefaultOkUiCommand("OnRemove", this); //$NON-NLS-1$
        model.getCommands().add(tempVar);
        UICommand tempVar2 = UICommand.createCancelUiCommand("Cancel", this); //$NON-NLS-1$
        model.getCommands().add(tempVar2);
    }

    private void onRemove() {
        if (getSelectedItems() != null && getSelectedItems().size() > 0) {
            ConfirmationModel model = (ConfirmationModel) getWindow();

            if (model.getProgress() != null) {
                return;
            }

            ArrayList<ActionParametersBase> list = new ArrayList<>();
            for (Object perm : getSelectedItems()) {
                PermissionsOperationsParameters tempVar = new PermissionsOperationsParameters();
                tempVar.setPermission((Permission) perm);
                list.add(tempVar);
            }

            model.startProgress();

            Frontend.getInstance().runMultipleAction(ActionType.RemovePermission, list,
                    result -> {

                        ConfirmationModel localModel = (ConfirmationModel) result.getState();
                        localModel.stopProgress();
                        cancel();

                    }, model);
        }
        else {
            cancel();
        }
    }

    public void cancel() {
        setWindow(null);
    }

    @Override
    protected void onSelectedItemChanged() {
        super.onSelectedItemChanged();
        updateActionAvailability();
    }

    @Override
    protected void selectedItemsChanged() {
        super.selectedItemsChanged();
        updateActionAvailability();
    }

    private void updateActionAvailability() {
        boolean isInherited = false;

        Permission p = getSelectedItem();
        if (p != null && getEntity() != null) {
            isInherited = !p.getAdElementId().equals(getEntity().getId());
        }

        getRemoveCommand().setIsExecutionAllowed(!isInherited && (getSelectedItem() != null
                || (getSelectedItems() != null && getSelectedItems().size() > 0)));
        // User Permission uses the same action panel as all the permission models, but you can't
        // add, so we need to hide the add button.
        getAddCommand().setIsAvailable(false);
    }

    @Override
    public void executeCommand(UICommand command) {
        super.executeCommand(command);

        if (command == getRemoveCommand()) {
            remove();
        }
        if ("OnRemove".equals(command.getName())) { //$NON-NLS-1$
            onRemove();
        }
        if ("Cancel".equals(command.getName())) { //$NON-NLS-1$
            cancel();
        }
    }

    @Override
    protected String getListName() {
        return "UserPermissionListModel"; //$NON-NLS-1$
    }
}
