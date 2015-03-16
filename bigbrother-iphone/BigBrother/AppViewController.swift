//
//  ViewController.swift
//  BigBrother
//
//  Created by Liu Xinan on 15/3/15.
//  Copyright (c) 2015 Liu Xinan. All rights reserved.
//

import UIKit

class AppViewController: UIViewController {
    
    @IBOutlet weak var remainingTime: UILabel!
    let socket = SocketIOClient(socketURL: "big-brother-api.herokuapp.com")
    
    func addHandlers() {
        self.socket.on("connect") {[weak self] data, ack in
            println("Connected to server!")
            self?.socket.emit("register")
        }
        
        self.socket.on("send offer") {[weak self] data, ack in
            println("Received offer!")
            if let fileName = data![0] as? String {
                self?.performSegueWithIdentifier(fileName, sender: nil)
            }
        }
        
        self.socket.on("send voucher") {[weak self] data, ack in
            println("Received voucher!")
        }
        
        self.socket.on("late person") {[weak self] data, ack in
            println("Received alert!")
            var alert = UIAlertController(title: "Wake Up", message: "Your flight is taking off soon!", preferredStyle: UIAlertControllerStyle.Alert)
            alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.Default, handler: nil))
            self?.presentViewController(alert, animated: true, completion: nil)
            self?.remainingTime.text = "33m to departure"
        }
        
        self.socket.on("flight delay") {[weak self] data, ack in
            println("Received alert!")
            var alert = UIAlertController(title: "We Are Sorry", message: "Your flight is delayed to 7:30PM...", preferredStyle: UIAlertControllerStyle.Alert)
            alert.addAction(UIAlertAction(title: "WTF", style: UIAlertActionStyle.Default, handler: nil))
            self?.presentViewController(alert, animated: true, completion: nil)
            self?.remainingTime.text = "5h 20m to departure"
        }
        
        self.socket.on("disconnect") {[weak self] data, ack in
            println("Disconnected from server.")
        }
        
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        self.setNeedsStatusBarAppearanceUpdate()
        self.addHandlers()
        self.socket.connect()
        
        // Do any additional setup after loading the view, typically from a nib.
    }
    
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        if let pvc = segue.destinationViewController as? PopupViewController {
            if let identifier = segue.identifier {
                switch identifier {
                case "burgerking":
                    pvc.image = UIImage(named: "burgerking")
                    pvc.stars = "★★★"
                    pvc.left = "13 of 20 left"
                case "whiskey":
                    pvc.image = UIImage(named: "whiskey")
                    pvc.stars = "★★★★★"
                    pvc.left = "2 of 18 left"
                default: break
                }
            }
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    override func preferredStatusBarStyle() -> UIStatusBarStyle {
        return UIStatusBarStyle.LightContent
    }

}

