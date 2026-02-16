### Set up environment for CS6241 HW1
In this homework we will use an ubuntu virtual machine as the development environment. All required software including gcc, llvm, cmake, and ninja, has been installed in the virtual machine.
The virtual machine is maintained using VirtualBox, and we use another tool, Vagrant, to build and manage the virtual machine.

Please install Vagrant 2.3.4 and VirtualBox 7.0.6 through the following links.
* [Vagrant](https://developer.hashicorp.com/vagrant/downloads)
* [VirtualBox](https://www.virtualbox.org/wiki/Downloads)

After install these two tools, run following commands in the terminal (for Windows, please use PowerShell) to set up the virtual machine.

```bash
    mkdir <FOLDER_FOR_THE_VM>
    cd <FOLDER_FOR_THE_VM>
    vagrant box add lechenyu/cs6241  # initialize Vagrant and set up the virtual machine's image
    vagrant init lechenyu/cs6241     # a 'Vagrantfile' will be generated as the virtual machine's configuration
    vagrant up                       # launch the virtual machine
    vagrant ssh                      # connect to the virtual machine using ssh
```

**For Windows users:** you may encounter this error message when running ``vagrant up``:

    Command: ["startvm", "04736358-7951-4622-b9b9-2a9efc4982fd", "--type", "headless"]
    
    Stderr: VBoxManage.exe: error: RawFile#0 failed to create the raw output file /dev/null (VERR_PATH_NOT_FOUND)
    VBoxManage.exe: error: Details: code E_FAIL (0x80004005), component ConsoleWrap, interface IConsole

Such error can be resolved by adding following configurations in ``Vagrantfile`` and run ``vagrant up`` again.

    config.vm.provider "virtualbox" do |v|
      v.customize [ "modifyvm", :id, "--uartmode1", "disconnected" ]
    end


**For Mac users: if your machine uses Apple M1 chip, Vagrant may fail to set up the virtual machine due to the hardware incompatibility. If you encounter such issue, please view the M1 Mac page in this repo.**

Once Vagrant finishes the set-up, you can connect to the virtual machine using ``vagrant ssh``.

### Set up the repository for CS6241 HW1
The homework repo has been uploaded to Gatech github. To use GT github make sure you are in GT network or on the GT VPN. Check out the repo in the virtual machine using ``git``
    
    git clone https://github.gatech.edu/CS6241/Homework0.git <ROOT_OF_HW0_REPO>

### How to bulid the repository.
This process uses CMake and Ninja to build the repo. Please type the following commands in the terminal:

```bash
    cd <ROOT_OF_HW0_REPO>
    mkdir build
    cmake -DCMAKE_INSTALL_PREFIX=./install -B build -S . -G Ninja
    cd build && ninja install
```


### Edit and build HW0 using VS Code remote development plugin
We recommend using VS Code to edit and build HW0. You can also use other IDEs supporting remote development (e.g., [CLion](https://www.jetbrains.com/help/clion/remote-development.html)). For VS Code, the first step is adding the ssh configuration of the virtual machine into ``~/.ssh/config``. If you encounter issues with PowerShell's `Out-File -Append`, please append the output of vagrant ssh-config to you .ssh\config file manually.

```bash
    # Linux/Mac
    cd <FOLDER_FOR_THE_VM>
    vagrant up
    vagrant ssh-config >> ~/.ssh/config
    
    # Windows PowerShell
    cd <FOLDER_FOR_THE_VM>
    vagrant up
    vagrant ssh-config | Out-File C:\Users\<USER_NAME>\.ssh\config -Append # replace <USER_NAME> with your user name
```

The next step is launching VS Code and installing necessary plugins. Please check the "Extensions" tab on the left and install the following three plugins if not installed.
* [Remote-SSH](https://marketplace.visualstudio.com/items?itemName=ms-vscode-remote.remote-ssh)
* [C/C++](https://marketplace.visualstudio.com/items?itemName=ms-vscode.cpptools)
* [CMake Tools](https://marketplace.visualstudio.com/items?itemName=ms-vscode.cmake-tools)

With all plugins ready, you can connect to the virtual machine through the "Remote Explorer" tab on the left. Click the item "default" under "SSH TARGETS" ('default' is the virtual machine's name assigned by ``vagrant ssh-config``), and then select "Connect to Host in Current Window". 

After successfully connecting to the virtual machine, open the HW0 repo through the menu bar ("File" -> "Open Folder.." -> type in the path to the HW0 repo and click "OK"). Now you are able to open and edit any files in the HW0 repo using VS Code.

We already set up CMake options for HW0 (see configuration in .vscode), incluing the locations of the "build" and "install" folders.
To build HW0, press Ctrl+Shift+P (Cmd+Shift+P for Mac), then select "CMake: Install". VS Code will automatically configure and build HW0, installing the passes into ``<ROOT_OF_HW0_REPO>/install``. To execute those scripts in ``<ROOT_OF_HW0_REPO>/install``, you can use either the system terminal or the [integrated terminal](https://code.visualstudio.com/docs/terminal/basics) in VS Code.
    
### Transfer files between the virtual machine and host machine
Vagrant syncs the folder ``/vagrant`` on the virtual machine with the folder ``<FOLDER_FOR_THE_VM>`` on the host machine. You can use these synced folders to tranfer files.