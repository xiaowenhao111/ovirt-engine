package org.ovirt.engine.ui.webadmin.section.main.view.tab.host;

import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import org.ovirt.engine.core.common.businessentities.VDS;
import org.ovirt.engine.core.common.businessentities.VM;
import org.ovirt.engine.core.common.businessentities.VMStatus;
import org.ovirt.engine.ui.common.idhandler.ElementIdHandler;
import org.ovirt.engine.ui.common.uicommon.model.SearchableDetailModelProvider;
import org.ovirt.engine.ui.common.widget.table.column.AbstractEnumColumn;
import org.ovirt.engine.ui.common.widget.table.column.AbstractTextColumn;
import org.ovirt.engine.ui.uicommonweb.models.hosts.HostListModel;
import org.ovirt.engine.ui.uicommonweb.models.hosts.HostVmListModel;
import org.ovirt.engine.ui.webadmin.ApplicationConstants;
import org.ovirt.engine.ui.webadmin.gin.AssetProvider;
import org.ovirt.engine.ui.webadmin.section.main.presenter.tab.host.SubTabHostVmPresenter;
import org.ovirt.engine.ui.webadmin.section.main.view.AbstractSubTabTableView;
import org.ovirt.engine.ui.webadmin.widget.table.column.AbstractUptimeColumn;
import org.ovirt.engine.ui.webadmin.widget.table.column.ColumnResizeTableLineChartProgressBar;
import org.ovirt.engine.ui.webadmin.widget.table.column.VmStatusColumn;
import org.ovirt.engine.ui.webadmin.widget.table.column.VmTypeColumn;

import com.google.gwt.core.client.GWT;

public class SubTabHostVmView extends AbstractSubTabTableView<VDS, VM, HostListModel<Void>, HostVmListModel>
        implements SubTabHostVmPresenter.ViewDef {

    interface ViewIdHandler extends ElementIdHandler<SubTabHostVmView> {
        ViewIdHandler idHandler = GWT.create(ViewIdHandler.class);
    }

    private static final ApplicationConstants constants = AssetProvider.getConstants();

    @Inject
    public SubTabHostVmView(SearchableDetailModelProvider<VM, HostListModel<Void>, HostVmListModel> modelProvider) {
        super(modelProvider);
        initTable();
        initWidget(getTableContainer());
    }

    @Override
    protected void generateIds() {
        ViewIdHandler.idHandler.generateAndSetIds(this);
    }

    void initTable() {
        getTable().enableColumnResizing();

        VmStatusColumn<VM> statusIconColumn = new VmStatusColumn<>();
        statusIconColumn.setContextMenuTitle(constants.statusIconVm());
        getTable().addColumn(statusIconColumn, constants.empty(), "30px"); //$NON-NLS-1$

        AbstractTextColumn<VM> nameColumn = new AbstractTextColumn<VM>() {
            @Override
            public String getValue(VM object) {
                return object.getName();
            }
        };
        nameColumn.makeSortable();
        getTable().addColumn(nameColumn, constants.nameVm(), "160px"); //$NON-NLS-1$

        VmTypeColumn typeColumn = new VmTypeColumn();
        typeColumn.setContextMenuTitle(constants.typeVm());
        getTable().addColumn(typeColumn, constants.empty(), "60px"); //$NON-NLS-1$

        AbstractTextColumn<VM> clusterColumn = new AbstractTextColumn<VM>() {
            @Override
            public String getValue(VM object) {
                return object.getClusterName();
            }
        };
        clusterColumn.makeSortable();
        getTable().addColumn(clusterColumn, constants.clusterVm(), "160px"); //$NON-NLS-1$

        AbstractTextColumn<VM> ipColumn = new AbstractTextColumn<VM>() {
            @Override
            public String getValue(VM object) {
                return object.getIp();
            }
        };
        ipColumn.makeSortable();
        getTable().addColumn(ipColumn, constants.ipVm(), "200px"); //$NON-NLS-1$

        AbstractTextColumn<VM> fqdnColumn = new AbstractTextColumn<VM>() {
            @Override
            public String getValue(VM object) {
                return object.getFqdn();
            }
        };
        fqdnColumn.makeSortable();
        getTable().addColumn(fqdnColumn, constants.fqdn(), "200px"); //$NON-NLS-1$

        getTable().addColumn(new ColumnResizeTableLineChartProgressBar<VM>(
                getTable(),
                new ResourceConsumptionComparator() {

                    @Override
                    protected Integer extractValue(VM vm) {
                        return vm.getUsageMemPercent();
                    }
                }) {
            @Override
            protected List<Integer> getProgressValues(VM object) {
                return object.getMemoryUsageHistory();
            }
        }, constants.memoryVm(), "120px"); //$NON-NLS-1$

        getTable().addColumn(new ColumnResizeTableLineChartProgressBar<VM>(
                getTable(),
                new ResourceConsumptionComparator() {
                    @Override
                    protected Integer extractValue(VM vm) {
                        return vm.getUsageCpuPercent();
                    }
                }) {
            @Override
            protected List<Integer> getProgressValues(VM object) {
                return object.getCpuUsageHistory();
            }
        }, constants.cpuVm(), "120px"); //$NON-NLS-1$

        getTable().addColumn(new ColumnResizeTableLineChartProgressBar<VM>(
                getTable(),
                new ResourceConsumptionComparator() {
                    @Override
                    protected Integer extractValue(VM vm) {
                        return vm.getUsageNetworkPercent();
                    }
                }) {
            @Override
            protected List<Integer> getProgressValues(VM object) {
                return object.getNetworkUsageHistory();
            }
        }, constants.networkVm(), "120px"); //$NON-NLS-1$

        AbstractTextColumn<VM> statusColumn = new AbstractEnumColumn<VM, VMStatus>() {
            @Override
            protected VMStatus getRawValue(VM object) {
                // check, if the current host is a target for the migration, then override status
                final VDS vds = getDetailModel().getEntity();
                if (object.getStatus().equals(VMStatus.MigratingFrom) && vds != null && vds.getId().equals(object.getMigratingToVds())) {
                    return VMStatus.MigratingTo;
                }

                return object.getStatus();
            }
        };
        statusColumn.makeSortable();
        getTable().addColumn(statusColumn, constants.statusVm(), "130px"); //$NON-NLS-1$

        AbstractTextColumn<VM> uptimeColumn = new AbstractUptimeColumn<VM>() {
            @Override
            protected Double getRawValue(VM object) {
                return object.getElapsedTime();
            }
        };
        uptimeColumn.makeSortable();
        getTable().addColumn(uptimeColumn, constants.uptimeVm(), "110px"); //$NON-NLS-1$
    }

    abstract class ResourceConsumptionComparator implements Comparator<VM> {

        @Override
        public int compare(VM vm1, VM vm2) {
            Integer val1 = extractValue(vm1) != null ? extractValue(vm1) : 0;
            Integer val2 = extractValue(vm2) != null ? extractValue(vm2) : 0;
            return val1.compareTo(val2);
        }

        protected abstract Integer extractValue(VM vm);
    }
}
