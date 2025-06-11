base_dir=$(abspath .)
sim_dir=$(abspath .)

include variables.mk
include sims/vcs/vcs.mk
include common.mk

FILELIST_NAME ?= synth

SYN_FILELIST = $(FILELIST_NAME).flist

ifneq ($(CUSTOM_VLOG), )
	RTL_DEPS = $(CUSTOM_VLOG)
else
	RTL_DEPS = $(TOP_MODS_FILELIST) $(TOP_SMEMS_FILE) $(EXT_FILELISTS)
endif

$(SYN_FILELIST): $(RTL_DEPS)
ifneq ($(CUSTOM_VLOG), )
	> $(SYN_FILELIST)
	$(foreach file,$^,echo $(file) >> $(SYN_FILELIST))
else
	cat $(TOP_MODS_FILELIST) | sort -u > $(SYN_FILELIST)
	echo $(TOP_SMEMS_FILE) >> $(SYN_FILELIST)
ifneq ($(EXT_FILELISTS),)
	cat $(EXT_FILELISTS) >> $(SYN_FILELIST)
endif
endif

.PHONY: rtl_filelist
rtl_filelist: $(SYN_FILELIST)